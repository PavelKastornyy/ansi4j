/*
 * Copyright 2022 Pavel Kastornyy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pk.ansi4j.css.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.function.Function;
import pk.ansi4j.core.api.function.FunctionArgument;
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction;
import pk.ansi4j.core.api.iso6429.SgrParameterValue;
import pk.ansi4j.css.DefaultAttributeValue;
import pk.ansi4j.css.api.Attribute;
import pk.ansi4j.css.api.AttributeChange;
import pk.ansi4j.css.api.AttributeContext;
import pk.ansi4j.css.api.color.Color16;
import pk.ansi4j.css.api.color.Color8;
import pk.ansi4j.css.api.color.SgrExtraColorValue;
import pk.ansi4j.css.api.AttributeResolver;
import pk.ansi4j.css.api.AttributeValue;
import pk.ansi4j.css.api.color.PaletteType;
import pk.ansi4j.css.api.text.TextAttribute;
import pk.ansi4j.css.api.text.TextAttributeConfig;
import pk.ansi4j.css.api.text.TextAttributeIndex;
import pk.ansi4j.css.api.text.TextAttributeValue;

/**
 * Currently supports only SGR functions.
 *
 * @author Pavel Kastornyy
 */
public class DefaultTextAttributeResolver implements AttributeResolver {

    /**
     * Text attribute processor.
     */
    @FunctionalInterface
    private static interface ArgumentProcessor {

        /**
         *
         * @param argumentValue
         * @param argumentQueue
         * @param newContextMap
         * @return list of changes empty of filled.
         */
        List<AttributeChange> proccess(FunctionArgument argument, Queue<FunctionArgument> argumentQueue,
            AttributeContext context, TextAttributeDescriptor descriptor);
    }

    private static class TextAttributeDescriptor {

        private final ArgumentProcessor processor;

        private final Attribute attribute;

        private final int value;

        public TextAttributeDescriptor(ArgumentProcessor processor, Attribute attribute, int value) {
            this.processor = processor;
            this.attribute = attribute;
            this.value = value;
        }

        public ArgumentProcessor getProcessor() {
            return processor;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public int getValue() {
            return value;
        }

    }

    /**
     * Descriptors by parameter values. This map hold data from converting from SGR standard to our TextAttribute
     * standard.
     */
    private final Map<Integer, TextAttributeDescriptor> descriptorsByParameterValue = new HashMap<>();

    /**
     * Common processor. Processes all attributes that have simple values.
     */
    private final ArgumentProcessor baseProcessor = (argument, argumentQueue, context, descriptor) -> {
        var attribute = descriptor.getAttribute();
        List<AttributeChange> changes = new ArrayList<>();
        var newValue = new DefaultAttributeValue(null, descriptor.getValue());
        var change = context.setAttribute(attribute, newValue);
        if (change.isPresent()) {
            changes.add(change.get());
        }
        return changes;
    };

    /**
     * Font processor. Processes font values.
     */
    private final ArgumentProcessor fontProcessor = (argument, argumentQueue, context, descriptor) -> {
        var attribute = descriptor.getAttribute();
        List<AttributeChange> changes = new ArrayList<>();
        TextAttributeConfig config = (TextAttributeConfig) context.getAttributeConfig(TextAttribute.class);
        var index = descriptor.getValue();
        var font = config.getFontFamilies().get(index);
        var newValue = new DefaultAttributeValue(index, font);
        var change = context.setAttribute(attribute, newValue);
        if (change.isPresent()) {
            changes.add(change.get());
        }
        return changes;
    };

    /**
     * Color processor. It processes foreground and background colors.
     */
    private final ArgumentProcessor colorProcessor = (argument, argumentQueue, context, descriptor) -> {
        var argumentValue = (int) argument.getValue();
        var attribute = descriptor.getAttribute();
        List<AttributeChange> changes = new ArrayList<>();
        TextAttributeConfig config = (TextAttributeConfig) context.getAttributeConfig(TextAttribute.class);
        //now we find new value
        AttributeValue newValue = null;
        if (!config.areExtraColorsEnabled()) {
            var colorIndex = descriptor.getValue();
            var color = config.getPalette8().getColors()[colorIndex];
            newValue = new DefaultAttributeValue(colorIndex, color, PaletteType.PALETTE_8);
        } else {
            if (argumentValue == SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE ||
                argumentValue == SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE) {
                //now we need second argument.
                int nextArgumentValue = (int) argumentQueue.peek().getValue();
                if (nextArgumentValue == SgrExtraColorValue.PALETTE_8_BIT) {
                    argumentQueue.poll();//now remove
                    int colorIndex = (int) argumentQueue.poll().getValue();
                    var color = config.getPalette256().getColors()[colorIndex];
                    newValue = new DefaultAttributeValue(colorIndex, color, PaletteType.PALETTE_256);
                } else if (nextArgumentValue == SgrExtraColorValue.PALETTE_24_BIT) {
                    argumentQueue.poll();//now remove
                    int red = (int) argumentQueue.poll().getValue();
                    int green = (int) argumentQueue.poll().getValue();
                    int blue = (int) argumentQueue.poll().getValue();
                    var color = (red << 16) | (green << 8) | blue;
                    newValue = new DefaultAttributeValue(null, color);
                }
            } else {
                var colorIndex = descriptor.getValue();
                var color = config.getPalette16().getColors()[colorIndex];
                newValue = new DefaultAttributeValue(colorIndex, color, PaletteType.PALETTE_16);
            }
        }
        var change = context.setAttribute(attribute, newValue);
        if (change.isPresent()) {
            changes.add(change.get());
        }
        return changes;
    };

    /**
     * Reset processor. Note - default values are not saved to context.
     */
    private final ArgumentProcessor resetProcessor = (argument, argumentQueue, context, descriptor) -> {
        List<AttributeChange> changes = new ArrayList<>();
        var attributes = new HashSet<>(context.getNonDefaultValuesByAttribute().keySet());
        for (var attribute : attributes) {
            if (attribute instanceof TextAttribute) {
                var change = context.setAttribute(attribute, null);
                if (change.isPresent()) {
                    changes.add(change.get());
                }
            }
        }
        return changes;
    };

    /**
     * Constructor.
     */
    public DefaultTextAttributeResolver() {
        this.initDescriptorsWithResetProcessor();
        this.initDescriptorsWithBaseProcessor();
        this.initDescriptorsWithFontProcessor();
        this.initDescriptorsWithBaseColorProcessor();
        this.initDescriptorsWithExtraColorProcessor();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttributeChange> resolve(FunctionFragment functionFragment, AttributeContext context) {
        //we need fifo queue, because every attribute processing function can retrieve additional arguments.
        Queue<FunctionArgument> argumentQueue = new LinkedList<>(functionFragment.getArguments());
        List<AttributeChange> totalChanges = new ArrayList<>();
        while (!argumentQueue.isEmpty()) {
            var argument = argumentQueue.poll();
            int argumentValue = (int) argument.getValue();
            var descriptor = this.descriptorsByParameterValue.get(argumentValue);
            if (descriptor != null) {
                var processor = descriptor.getProcessor();
                var changes = processor.proccess(argument, argumentQueue, context, descriptor);
                totalChanges.addAll(changes);
            }
        }
        return totalChanges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function getTargetFunction() {
        return ControlSequenceFunction.SGR_SELECT_GRAPHIC_RENDITION;
    }

    private void initDescriptorsWithResetProcessor() {
        var m = this.descriptorsByParameterValue;

        m.put(SgrParameterValue.DEFAULT_RENDITION,
                new TextAttributeDescriptor(resetProcessor, null, -1));
    }

    private void initDescriptorsWithBaseProcessor() {
        var m = this.descriptorsByParameterValue;

        var key = TextAttribute.WEIGHT;
        m.put(SgrParameterValue.BOLD_OR_INCREASED_INTENSITY,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.WEIGHT_BOLD));
        m.put(SgrParameterValue.FAINT_OR_DECREASED_INTENSITY_OR_SECOND_COLOUR,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.WEIGHT_FAINT));
        m.put(SgrParameterValue.NORMAL_COLOUR_OR_NORMAL_INTENSITY,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.WEIGHT_NORMAL));

        key = TextAttribute.ITALIC;
        m.put(SgrParameterValue.ITALICIZED,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.ITALIC_ON));
        m.put(SgrParameterValue.NOT_ITALICIZED_OR_NOT_FRAKTUR,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.ITALIC_OFF));

        key = TextAttribute.UNDERLINE;
        m.put(SgrParameterValue.SINGLY_UNDERLINED,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.UNDERLINE_SINGLE));
        m.put(SgrParameterValue.DOUBLY_UNDERLINED,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.UNDERLINE_DOUBLE));
        m.put(SgrParameterValue.NOT_UNDERLINED,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.UNDERLINE_OFF));

        key = TextAttribute.BLINKING;
        m.put(SgrParameterValue.SLOWLY_BLINKING,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.BLINKING_SLOW));
        m.put(SgrParameterValue.RAPIDLY_BLINKING,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.BLINKING_RAPID));
        m.put(SgrParameterValue.STEADY,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.BLINKING_OFF));

        key = TextAttribute.REVERSE_VIDEO;
        m.put(SgrParameterValue.NEGATIVE_IMAGE,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.REVERSE_VIDEO_ON));
        m.put(SgrParameterValue.POSITIVE_IMAGE,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.REVERSE_VIDEO_OFF));

        key = TextAttribute.VISIBILITY;
        m.put(SgrParameterValue.CONCEALED_CHARACTERS,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.VISIBILITY_HIDDEN));
        m.put(SgrParameterValue.REVEALED_CHARACTERS,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.VISIBILITY_VISIBLE));

        key = TextAttribute.STRIKETHROUGH;
        m.put(SgrParameterValue.CROSSED_OUT,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.STRIKETHROUGH_ON));
        m.put(SgrParameterValue.NOT_CROSSED_OUT,
                new TextAttributeDescriptor(baseProcessor, key, TextAttributeValue.STRIKETHROUGH_OFF));

    }

    private void initDescriptorsWithFontProcessor() {
        var m = this.descriptorsByParameterValue;

        var key = TextAttribute.FONT;
        m.put(SgrParameterValue.PRIMARY_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_DEFAULT));
        m.put(SgrParameterValue.FIRST_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_1));
        m.put(SgrParameterValue.SECOND_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_2));
        m.put(SgrParameterValue.THIRD_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_3));
        m.put(SgrParameterValue.FOURTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_4));
        m.put(SgrParameterValue.FIFTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_5));
        m.put(SgrParameterValue.SIXTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_6));
        m.put(SgrParameterValue.SEVENTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_7));
        m.put(SgrParameterValue.EIGHTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_8));
        m.put(SgrParameterValue.NINTH_ALTERNATIVE_FONT,
                new TextAttributeDescriptor(fontProcessor, key, TextAttributeIndex.FONT_ALTERNATIVE_9));
    }

    private void initDescriptorsWithBaseColorProcessor() {
        var m = this.descriptorsByParameterValue;

        var attribute = TextAttribute.FOREGROUND_COLOR;
        m.put(SgrParameterValue.BLACK_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.BLACK));
        m.put(SgrParameterValue.RED_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.RED));
        m.put(SgrParameterValue.GREEN_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.GREEN));
        m.put(SgrParameterValue.YELLOW_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.YELLOW));
        m.put(SgrParameterValue.BLUE_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.BLUE));
        m.put(SgrParameterValue.MAGENTA_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.MAGENTA));
        m.put(SgrParameterValue.CYAN_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.CYAN));
        m.put(SgrParameterValue.WHITE_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.WHITE));

        attribute = TextAttribute.BACKGROUND_COLOR;
        m.put(SgrParameterValue.BLACK_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.BLACK));
        m.put(SgrParameterValue.RED_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.RED));
        m.put(SgrParameterValue.GREEN_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.GREEN));
        m.put(SgrParameterValue.YELLOW_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.YELLOW));
        m.put(SgrParameterValue.BLUE_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.BLUE));
        m.put(SgrParameterValue.MAGENTA_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.MAGENTA));
        m.put(SgrParameterValue.CYAN_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.CYAN));
        m.put(SgrParameterValue.WHITE_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, attribute, Color8.WHITE));
    }

    private void initDescriptorsWithExtraColorProcessor() {
        var m = this.descriptorsByParameterValue;
        //4 bit palette
        var key = TextAttribute.FOREGROUND_COLOR;
        m.put(SgrExtraColorValue.BRIGHT_BLACK_DISPLAY,
            new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_BLACK));
        m.put(SgrExtraColorValue.BRIGHT_RED_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_RED));
        m.put(SgrExtraColorValue.BRIGHT_GREEN_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_GREEN));
        m.put(SgrExtraColorValue.BRIGHT_YELLOW_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_YELLOW));
        m.put(SgrExtraColorValue.BRIGHT_BLUE_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_BLUE));
        m.put(SgrExtraColorValue.BRIGHT_MAGENTA_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_MAGENTA));
        m.put(SgrExtraColorValue.BRIGHT_CYAN_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_CYAN));
        m.put(SgrExtraColorValue.BRIGHT_WHITE_DISPLAY,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_WHITE));

        key = TextAttribute.BACKGROUND_COLOR;
        m.put(SgrExtraColorValue.BRIGHT_BLACK_BACKGROUND,
            new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_BLACK));
        m.put(SgrExtraColorValue.BRIGHT_RED_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_RED));
        m.put(SgrExtraColorValue.BRIGHT_GREEN_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_GREEN));
        m.put(SgrExtraColorValue.BRIGHT_YELLOW_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_YELLOW));
        m.put(SgrExtraColorValue.BRIGHT_BLUE_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_BLUE));
        m.put(SgrExtraColorValue.BRIGHT_MAGENTA_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_MAGENTA));
        m.put(SgrExtraColorValue.BRIGHT_CYAN_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_CYAN));
        m.put(SgrExtraColorValue.BRIGHT_WHITE_BACKGROUND,
                new TextAttributeDescriptor(colorProcessor, key, Color16.BRIGHT_WHITE));

        //8 bit and 24 bit palette
        key = TextAttribute.FOREGROUND_COLOR;
        m.put(SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE,
            new TextAttributeDescriptor(colorProcessor, key, -1));

        key = TextAttribute.BACKGROUND_COLOR;
        m.put(SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE,
            new TextAttributeDescriptor(colorProcessor, key, -1));
    }
}



