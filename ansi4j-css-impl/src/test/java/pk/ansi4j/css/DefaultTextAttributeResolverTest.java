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
package pk.ansi4j.css;

import java.util.ArrayList;
import java.util.HashMap;
import pk.ansi4j.css.color.XtermPalette256;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pk.ansi4j.css.api.Attribute;
import org.junit.jupiter.params.provider.Arguments;
import pk.ansi4j.core.api.function.FunctionArgument;
import pk.ansi4j.core.api.iso6429.SgrParameterValue;
import pk.ansi4j.css.api.text.TextAttribute;
import pk.ansi4j.css.api.text.TextAttributeValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import pk.ansi4j.css.api.AttributeValue;
import pk.ansi4j.css.api.color.Color16;
import pk.ansi4j.css.api.color.Color8;
import pk.ansi4j.css.api.color.PaletteType;
import pk.ansi4j.css.api.color.SgrExtraColorValue;
import pk.ansi4j.css.api.text.TextAttributeConfig;
import pk.ansi4j.css.api.text.TextAttributeIndex;
import pk.ansi4j.css.text.DefaultTextAttributeConfig;
import pk.ansi4j.css.text.DefaultTextAttributeResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultTextAttributeResolverTest {

    /**
     * Black.
     */
    private static final int defaultFgColor = 0x000000;

    /**
     * White (not bright white that is ffffff).
     */
    private static final int defaultBgColor = 0xc0c0c0;

    private static TextAttributeConfig baseConfig;

    private static TextAttributeConfig extraColorConfig;

    @BeforeAll
    public static void init() {
       baseConfig = new DefaultTextAttributeConfig.Builder()
            .defaultForegroundColor(0, PaletteType.PALETTE_8)
            .defaultBackgroundColor(defaultBgColor)
            .fontFamilies(List.of("Arial", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"))
            .palette8(new XtermPalette256())
            .build();

       extraColorConfig = new DefaultTextAttributeConfig.Builder()
            .defaultForegroundColor(defaultFgColor)
            .defaultBackgroundColor(defaultBgColor)
            .fontFamilies(List.of("Arial", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"))
            .extraColorsEnabled(true)
            .palette16(new XtermPalette256())
            .palette256(new XtermPalette256())
            .build();
    }

    private static final class TestParameter {

        private final TextAttributeConfig config;

        private final Object sgrParameterValue;

        private final Attribute attributeKey;

        private final int oldValue;//or oldIndex

        private final int newValue;//or oldIndex

        public TestParameter(Object sgrParameterValue, TextAttributeConfig config,
                Attribute attributeKey, int oldValue, int newValue) {
            this.config = config;
            this.sgrParameterValue = sgrParameterValue;
            this.attributeKey = attributeKey;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public TextAttributeConfig getConfig() {
            return config;
        }

        public Object getSgrParameterValue() {
            return sgrParameterValue;
        }

        public Attribute getAttribute() {
            return attributeKey;
        }

        public int getOldValue() {
            return oldValue;
        }

        public int getNewValue() {
            return newValue;
        }

    }

    public static Stream<Arguments> provideSetNonDefaultAttributeTestParameters() {
        return Stream.of(
            Arguments.of(new TestParameter(
                    SgrParameterValue.BOLD_OR_INCREASED_INTENSITY,
                    baseConfig,
                    TextAttribute.WEIGHT,
                    TextAttributeValue.WEIGHT_NORMAL,
                    TextAttributeValue.WEIGHT_BOLD)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.ITALICIZED,
                    baseConfig,
                    TextAttribute.ITALIC,
                    TextAttributeValue.ITALIC_OFF,
                    TextAttributeValue.ITALIC_ON)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.SINGLY_UNDERLINED,
                    baseConfig,
                    TextAttribute.UNDERLINE,
                    TextAttributeValue.UNDERLINE_OFF,
                    TextAttributeValue.UNDERLINE_SINGLE)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.SLOWLY_BLINKING,
                    baseConfig,
                    TextAttribute.BLINKING,
                    TextAttributeValue.BLINKING_OFF,
                    TextAttributeValue.BLINKING_SLOW)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.NEGATIVE_IMAGE,
                    baseConfig,
                    TextAttribute.REVERSE_VIDEO,
                    TextAttributeValue.REVERSE_VIDEO_OFF,
                    TextAttributeValue.REVERSE_VIDEO_ON)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.CONCEALED_CHARACTERS,
                    baseConfig,
                    TextAttribute.VISIBILITY,
                    TextAttributeValue.VISIBILITY_VISIBLE,
                    TextAttributeValue.VISIBILITY_HIDDEN)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.CROSSED_OUT,
                    baseConfig,
                    TextAttribute.STRIKETHROUGH,
                    TextAttributeValue.STRIKETHROUGH_OFF,
                    TextAttributeValue.STRIKETHROUGH_ON)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.FIRST_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_DEFAULT,
                    TextAttributeIndex.FONT_ALTERNATIVE_1)),

            //3bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.RED_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    defaultFgColor,
                    baseConfig.getPalette8().getColors()[Color8.RED])),
            //4bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.RED_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    defaultFgColor,
                    extraColorConfig.getPalette16().getColors()[Color16.RED])),
            //8bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";58",
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    defaultFgColor,
                    extraColorConfig.getPalette256().getColors()[58])),
            //24bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";"
                            + SgrExtraColorValue.PALETTE_24_BIT + ";47;5;159",
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    defaultFgColor,
                    0x2F059F)),

            //3bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.BLACK_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    defaultBgColor,
                    baseConfig.getPalette8().getColors()[Color8.BLACK])),
            //4bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.BLACK_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    defaultBgColor,
                    extraColorConfig.getPalette16().getColors()[Color16.BLACK])),
            //8bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";58",
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    defaultBgColor,
                    extraColorConfig.getPalette256().getColors()[58])),
            //24bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";"
                            + SgrExtraColorValue.PALETTE_24_BIT + ";47;5;159",
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    defaultBgColor,
                    0x2F059F))

          );
    }

    public static Stream<Arguments> provideChangeNonDefaultAttributeTestParameters() {
        return Stream.of(
            Arguments.of(new TestParameter(
                    SgrParameterValue.FAINT_OR_DECREASED_INTENSITY_OR_SECOND_COLOUR,
                    baseConfig,
                    TextAttribute.WEIGHT,
                    TextAttributeValue.WEIGHT_BOLD,
                    TextAttributeValue.WEIGHT_FAINT)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.DOUBLY_UNDERLINED,
                    baseConfig,
                    TextAttribute.UNDERLINE,
                    TextAttributeValue.UNDERLINE_SINGLE,
                    TextAttributeValue.UNDERLINE_DOUBLE)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.RAPIDLY_BLINKING,
                    baseConfig,
                    TextAttribute.BLINKING,
                    TextAttributeValue.BLINKING_SLOW,
                    TextAttributeValue.BLINKING_RAPID)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.SECOND_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_1,
                    TextAttributeIndex.FONT_ALTERNATIVE_2)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.THIRD_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_2,
                    TextAttributeIndex.FONT_ALTERNATIVE_3)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.FOURTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_3,
                    TextAttributeIndex.FONT_ALTERNATIVE_4)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.FIFTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_4,
                    TextAttributeIndex.FONT_ALTERNATIVE_5)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.SIXTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_5,
                    TextAttributeIndex.FONT_ALTERNATIVE_6)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.SEVENTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_6,
                    TextAttributeIndex.FONT_ALTERNATIVE_7)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.EIGHTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_7,
                    TextAttributeIndex.FONT_ALTERNATIVE_8)),
            Arguments.of(new TestParameter(
                    SgrParameterValue.NINTH_ALTERNATIVE_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_8,
                    TextAttributeIndex.FONT_ALTERNATIVE_9)),

            //3 bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.GREEN_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.RED],
                    baseConfig.getPalette8().getColors()[Color8.GREEN])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.YELLOW_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.GREEN],
                    baseConfig.getPalette8().getColors()[Color8.YELLOW])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.BLUE_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.YELLOW],
                    baseConfig.getPalette8().getColors()[Color8.BLUE])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.MAGENTA_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.BLUE],
                    baseConfig.getPalette8().getColors()[Color8.MAGENTA])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.CYAN_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.MAGENTA],
                    baseConfig.getPalette8().getColors()[Color8.CYAN])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.WHITE_DISPLAY,
                    baseConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.CYAN],
                    baseConfig.getPalette8().getColors()[Color8.WHITE])),
            //4 bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_BLACK_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.WHITE],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLACK])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_RED_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLACK],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_RED])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_GREEN_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_RED],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_GREEN])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_YELLOW_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_GREEN],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_YELLOW])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_BLUE_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_YELLOW],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLUE])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_MAGENTA_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLUE],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_MAGENTA])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_CYAN_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_MAGENTA],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_CYAN])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_WHITE_DISPLAY,
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_CYAN],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_WHITE])),
            //8bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";59",
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    extraColorConfig.getPalette256().getColors()[58],
                    extraColorConfig.getPalette256().getColors()[59])),
            //24bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";"
                            + SgrExtraColorValue.PALETTE_24_BIT + ";1;45;159",
                    extraColorConfig,
                    TextAttribute.FOREGROUND_COLOR,
                    0x2F059F,
                    0x012D9F)),

            //3 bit
            Arguments.of(new TestParameter(
                    SgrParameterValue.RED_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.BLACK],
                    baseConfig.getPalette8().getColors()[Color8.RED])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.GREEN_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.RED],
                    baseConfig.getPalette8().getColors()[Color8.GREEN])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.YELLOW_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.GREEN],
                    baseConfig.getPalette8().getColors()[Color8.YELLOW])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.BLUE_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.YELLOW],
                    baseConfig.getPalette8().getColors()[Color8.BLUE])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.MAGENTA_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.BLUE],
                    baseConfig.getPalette8().getColors()[Color8.MAGENTA])),
            Arguments.of(new TestParameter(
                    SgrParameterValue.CYAN_BACKGROUND,
                    baseConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    baseConfig.getPalette8().getColors()[Color8.MAGENTA],
                    baseConfig.getPalette8().getColors()[Color8.CYAN])),
            //4 bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_BLACK_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.WHITE],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLACK])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_RED_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLACK],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_RED])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_GREEN_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_RED],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_GREEN])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_YELLOW_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_GREEN],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_YELLOW])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_BLUE_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_YELLOW],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLUE])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_MAGENTA_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLUE],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_MAGENTA])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_CYAN_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_MAGENTA],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_CYAN])),
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BRIGHT_WHITE_BACKGROUND,
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_CYAN],
                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_WHITE])),
            //8bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";59",
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    extraColorConfig.getPalette256().getColors()[58],
                    extraColorConfig.getPalette256().getColors()[59])),
            //24bit
            Arguments.of(new TestParameter(
                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";"
                            + SgrExtraColorValue.PALETTE_24_BIT + ";1;45;159",
                    extraColorConfig,
                    TextAttribute.BACKGROUND_COLOR,
                    0x2F059F,
                    0x012D9F))
          );
    }

    public static Stream<Arguments> provideSetDefaultAttributeTestParameters() {
        return Stream.of(
            Arguments.of(new TestParameter(
                    SgrParameterValue.NORMAL_COLOUR_OR_NORMAL_INTENSITY,
                    baseConfig,
                    TextAttribute.WEIGHT,
                    TextAttributeValue.WEIGHT_BOLD,
                    TextAttributeValue.WEIGHT_NORMAL)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.NOT_ITALICIZED_OR_NOT_FRAKTUR,
                    baseConfig,
                    TextAttribute.ITALIC,
                    TextAttributeValue.ITALIC_ON,
                    TextAttributeValue.ITALIC_OFF)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.NOT_UNDERLINED,
                    baseConfig,
                    TextAttribute.UNDERLINE,
                    TextAttributeValue.UNDERLINE_SINGLE,
                    TextAttributeValue.UNDERLINE_OFF)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.STEADY,
                    baseConfig,
                    TextAttribute.BLINKING,
                    TextAttributeValue.BLINKING_SLOW,
                    TextAttributeValue.BLINKING_OFF)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.POSITIVE_IMAGE,
                    baseConfig,
                    TextAttribute.REVERSE_VIDEO,
                    TextAttributeValue.REVERSE_VIDEO_ON,
                    TextAttributeValue.REVERSE_VIDEO_OFF)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.REVEALED_CHARACTERS,
                    baseConfig,
                    TextAttribute.VISIBILITY,
                    TextAttributeValue.VISIBILITY_HIDDEN,
                    TextAttributeValue.VISIBILITY_VISIBLE)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.NOT_CROSSED_OUT,
                    baseConfig,
                    TextAttribute.STRIKETHROUGH,
                    TextAttributeValue.STRIKETHROUGH_ON,
                    TextAttributeValue.STRIKETHROUGH_OFF)),

            Arguments.of(new TestParameter(
                    SgrParameterValue.PRIMARY_FONT,
                    baseConfig,
                    TextAttribute.FONT,
                    TextAttributeIndex.FONT_ALTERNATIVE_9,
                    TextAttributeIndex.FONT_DEFAULT))
//
//            //3 bit
//            Arguments.of(new TestParameter(
//                    SgrParameterValue.BLACK_DISPLAY,
//                    baseConfig,
//                    TextAttribute.FOREGROUND_COLOR,
//                    baseConfig.getPalette8().getColors()[Color8.WHITE],
//                    (int) baseConfig.getForegroundDefaultColor().getValue())),
//            //4 bit
//            Arguments.of(new TestParameter(
//                    SgrParameterValue.BLACK_DISPLAY,
//                    extraColorConfig,
//                    TextAttribute.FOREGROUND_COLOR,
//                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_WHITE],
//                    defaultFgColor)),
//            //8bit
//            Arguments.of(new TestParameter(
//                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";0",
//                    extraColorConfig,
//                    TextAttribute.FOREGROUND_COLOR,
//                    extraColorConfig.getPalette256().getColors()[200],
//                    defaultFgColor)),
//            //24bit
//            Arguments.of(new TestParameter(
//                    SgrExtraColorValue.DISPLAY_8_OR_24_BIT_PALETTE + ";"
//                            + SgrExtraColorValue.PALETTE_24_BIT + ";0;0;0",
//                    extraColorConfig,
//                    TextAttribute.FOREGROUND_COLOR,
//                    0x2F059F,
//                    defaultFgColor)),
//
//            //3 bit
//            Arguments.of(new TestParameter(
//                    SgrParameterValue.WHITE_BACKGROUND,
//                    baseConfig,
//                    TextAttribute.BACKGROUND_COLOR,
//                    baseConfig.getPalette8().getColors()[Color8.BLACK],
//                    defaultBgColor)),
//            //4 bit
//            Arguments.of(new TestParameter(
//                    SgrParameterValue.WHITE_BACKGROUND,
//                    extraColorConfig,
//                    TextAttribute.BACKGROUND_COLOR,
//                    extraColorConfig.getPalette16().getColors()[Color16.BRIGHT_BLACK],
//                    defaultBgColor)),
//            //8bit
//            Arguments.of(new TestParameter(
//                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";" + SgrExtraColorValue.PALETTE_8_BIT + ";7",
//                    extraColorConfig,
//                    TextAttribute.BACKGROUND_COLOR,
//                    extraColorConfig.getPalette256().getColors()[200],
//                    defaultBgColor)),
//            //24bit
//            Arguments.of(new TestParameter(
//                    SgrExtraColorValue.BACKGROUND_8_OR_24_BIT_PALETTE + ";"
//                            + SgrExtraColorValue.PALETTE_24_BIT + ";192;192;192",
//                    extraColorConfig,
//                    TextAttribute.BACKGROUND_COLOR,
//                    0x2F059F,
//                    defaultBgColor))

          );
    }

    @ParameterizedTest
    @MethodSource("provideSetNonDefaultAttributeTestParameters")
    public void resolve_setNonDefaultAttribute_success(TestParameter parameter) {
        var context = new DefaultAttributeContext(List.of(parameter.config));
        var resolver = new DefaultTextAttributeResolver();
        List<FunctionArgument> arguments = this.createArguments(parameter);
        var changes = resolver.resolve(new FunctionFragmentImpl(arguments), context);
        assertThat(context.getNonDefaultValuesByAttribute(), aMapWithSize(1));
        if (parameter.getAttribute() != TextAttribute.FONT) {
            assertThat(context.getNonDefaultValuesByAttribute().get(parameter.attributeKey).getValue(),
                equalTo(parameter.getNewValue()));
        } else {
            assertThat(context.getNonDefaultValuesByAttribute().get(parameter.attributeKey).getIndex().getAsInt(),
                equalTo(parameter.getNewValue()));
        }
        assertThat(changes, hasSize(1));
        var change0 = changes.get(0);
        assertThat(change0.getAttribute(), equalTo(parameter.getAttribute()));
        if (parameter.getAttribute() != TextAttribute.FONT) {
            assertThat(change0.getOldValue().getValue(), equalTo(parameter.getOldValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.getNewValue()));
        } else {
            var oldIndex = change0.getOldValue().getIndex().getAsInt();
            assertThat(oldIndex, equalTo(parameter.getOldValue()));
            assertThat(change0.getOldValue().getValue(), equalTo(parameter.config.getFontFamilies().get(oldIndex)));
            var newIndex = change0.getNewValue().getIndex().getAsInt();
            assertThat(newIndex, equalTo(parameter.getNewValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.config.getFontFamilies().get(newIndex)));
        }

    }

    @ParameterizedTest
    @MethodSource("provideChangeNonDefaultAttributeTestParameters")
    public void resolve_changeNonDefaultAttribute_success(TestParameter parameter) {
        //creating context with "old" value
        var context = new DefaultAttributeContext(List.of(parameter.config));
        AttributeValue oldValue = null;
        if (parameter.getAttribute() != TextAttribute.FONT) {
            oldValue = new DefaultAttributeValue(null, parameter.oldValue);
        } else {
            oldValue = new DefaultAttributeValue(parameter.oldValue,
                    parameter.config.getFontFamilies().get(parameter.oldValue));
        }
        context.setAttribute(parameter.getAttribute(), oldValue);

        var resolver = new DefaultTextAttributeResolver();
        List<FunctionArgument> arguments = this.createArguments(parameter);
        var changes = resolver.resolve(new FunctionFragmentImpl(arguments), context);
        assertThat(context.getNonDefaultValuesByAttribute(), aMapWithSize(1));
        if (parameter.getAttribute() != TextAttribute.FONT) {
            assertThat(context.getNonDefaultValuesByAttribute().get(parameter.attributeKey).getValue(),
                equalTo(parameter.getNewValue()));
        } else {
            assertThat(context.getNonDefaultValuesByAttribute().get(parameter.attributeKey).getIndex().getAsInt(),
                equalTo(parameter.getNewValue()));
        }
        assertThat(changes, hasSize(1));
        var change0 = changes.get(0);
        assertThat(change0.getAttribute(), equalTo(parameter.getAttribute()));
        if (parameter.getAttribute() != TextAttribute.FONT) {
            assertThat(change0.getOldValue().getValue(), equalTo(parameter.getOldValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.getNewValue()));
        } else {
            assertThat(change0.getOldValue(), equalTo(oldValue));
            var newIndex = change0.getNewValue().getIndex().getAsInt();
            assertThat(newIndex, equalTo(parameter.getNewValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.config.getFontFamilies().get(newIndex)));
        }
    }

    @ParameterizedTest
    @MethodSource("provideSetDefaultAttributeTestParameters")
    public void resolve_setDefaultAttribute_success(TestParameter parameter) {
                //creating context with "old" value
        var context = new DefaultAttributeContext(List.of(parameter.config));
        AttributeValue oldValue = null;
        if (parameter.getAttribute() != TextAttribute.FONT) {
            oldValue = new DefaultAttributeValue(null, parameter.oldValue);
        } else {
            oldValue = new DefaultAttributeValue(parameter.oldValue,
                    parameter.config.getFontFamilies().get(parameter.oldValue));
        }
        context.setAttribute(parameter.getAttribute(), oldValue);

        var resolver = new DefaultTextAttributeResolver();
        List<FunctionArgument> arguments = this.createArguments(parameter);
        var changes = resolver.resolve(new FunctionFragmentImpl(arguments), context);
        assertThat(context.getNonDefaultValuesByAttribute(), aMapWithSize(0));
        assertThat(changes, hasSize(1));
        var change0 = changes.get(0);
        assertThat(change0.getAttribute(), equalTo(parameter.getAttribute()));
        if (parameter.getAttribute() != TextAttribute.FONT) {
            assertThat(change0.getOldValue().getValue(), equalTo(parameter.getOldValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.getNewValue()));
        } else {
            assertThat(change0.getOldValue(), equalTo(oldValue));
            var newIndex = change0.getNewValue().getIndex().getAsInt();
            assertThat(newIndex, equalTo(parameter.getNewValue()));
            assertThat(change0.getNewValue().getValue(), equalTo(parameter.config.getFontFamilies().get(newIndex)));
        }
    }

    @Test
    public void resolve_resetAll_success() {
        var context = new DefaultAttributeContext(List.of(extraColorConfig));


        context.setAttribute(TextAttribute.WEIGHT,
                new DefaultAttributeValue(null, TextAttributeValue.WEIGHT_BOLD));
        context.setAttribute(TextAttribute.ITALIC,
                new DefaultAttributeValue(null, TextAttributeValue.ITALIC_ON));
        context.setAttribute(TextAttribute.UNDERLINE,
                new DefaultAttributeValue(null, TextAttributeValue.UNDERLINE_DOUBLE));
        context.setAttribute(TextAttribute.BLINKING,
                new DefaultAttributeValue(null, TextAttributeValue.BLINKING_SLOW));
        context.setAttribute(TextAttribute.REVERSE_VIDEO,
                new DefaultAttributeValue(null, TextAttributeValue.REVERSE_VIDEO_ON));
        context.setAttribute(TextAttribute.VISIBILITY,
                new DefaultAttributeValue(null, TextAttributeValue.VISIBILITY_HIDDEN));
        context.setAttribute(TextAttribute.STRIKETHROUGH,
                new DefaultAttributeValue(null, TextAttributeValue.STRIKETHROUGH_ON));
        context.setAttribute(TextAttribute.FONT,
                new DefaultAttributeValue(TextAttributeIndex.FONT_ALTERNATIVE_3,
                        extraColorConfig.getFontFamilies().get(TextAttributeIndex.FONT_ALTERNATIVE_3)));
        context.setAttribute(TextAttribute.FOREGROUND_COLOR, extraColorConfig.getDefaultBackgroundColor());
        context.setAttribute(TextAttribute.BACKGROUND_COLOR, extraColorConfig.getDefaultForegroundColor());

        assertThat(context.getNonDefaultValuesByAttribute(), aMapWithSize(10));

        var resolver = new DefaultTextAttributeResolver();
        var arguments = new ArrayList<FunctionArgument>();
        arguments.add(new FunctionArgumentImpl(0, false));
        var changes = resolver.resolve(new FunctionFragmentImpl(arguments), context);
        assertThat(context.getNonDefaultValuesByAttribute(), aMapWithSize(0));

        assertThat(changes, hasSize(10));

        for (var change : changes) {
            switch ((TextAttribute) change.getAttribute()) {
                 case WEIGHT:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.WEIGHT_BOLD));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.WEIGHT_NORMAL));
                    break;
                    case ITALIC:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.ITALIC_ON));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.ITALIC_OFF));
                    break;
                    case UNDERLINE:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.UNDERLINE_DOUBLE));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.UNDERLINE_OFF));
                    break;
                    case BLINKING:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.BLINKING_SLOW));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.BLINKING_OFF));
                    break;
                    case REVERSE_VIDEO:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.REVERSE_VIDEO_ON));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.REVERSE_VIDEO_OFF));
                    break;
                    case VISIBILITY:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.VISIBILITY_HIDDEN));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.VISIBILITY_VISIBLE));
                    break;
                    case STRIKETHROUGH:
                        assertThat(change.getOldValue().getValue(), equalTo(TextAttributeValue.STRIKETHROUGH_ON));
                        assertThat(change.getNewValue().getValue(), equalTo(TextAttributeValue.STRIKETHROUGH_OFF));
                    break;
                    case FONT:
                        assertThat(change.getOldValue().getIndex().getAsInt(),
                                equalTo(TextAttributeIndex.FONT_ALTERNATIVE_3));
                        assertThat(change.getNewValue().getIndex().getAsInt(),
                                equalTo(TextAttributeIndex.FONT_DEFAULT));
                    break;
                    case FOREGROUND_COLOR:
                        assertThat(change.getOldValue(), equalTo(extraColorConfig.getDefaultBackgroundColor()));
                        assertThat(change.getNewValue(), equalTo(extraColorConfig.getDefaultForegroundColor()));
                    break;
                    case BACKGROUND_COLOR:
                        assertThat(change.getOldValue(), equalTo(extraColorConfig.getDefaultForegroundColor()));
                        assertThat(change.getNewValue(), equalTo(extraColorConfig.getDefaultBackgroundColor()));
                    break;
                    default:
                        throw new IllegalArgumentException("Unknown attribute=" + change.getAttribute());
            }
        }
    }

    private List<FunctionArgument> createArguments(TestParameter parameter) {
        var value = parameter.getSgrParameterValue();
        if (value instanceof String) {
            var splits = ((String) value).split(Pattern.quote(";"));
            var list = new ArrayList<FunctionArgument>();
            for (var split : splits) {
                list.add(new FunctionArgumentImpl(Integer.valueOf(split), false));
            }
            return list;
        } else {
            return List.of(new FunctionArgumentImpl(parameter.getSgrParameterValue(), false));
        }
    }

}

