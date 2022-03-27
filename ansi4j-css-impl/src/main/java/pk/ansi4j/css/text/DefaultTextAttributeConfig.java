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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pk.ansi4j.css.DefaultAttributeValue;
import pk.ansi4j.css.api.Attribute;
import pk.ansi4j.css.api.AttributeValue;
import pk.ansi4j.css.api.color.Palette16;
import pk.ansi4j.css.api.color.Palette256;
import pk.ansi4j.css.api.color.Palette8;
import pk.ansi4j.css.api.color.PaletteType;
import pk.ansi4j.css.api.text.TextAttribute;
import pk.ansi4j.css.api.text.TextAttributeConfig;
import pk.ansi4j.css.api.text.TextAttributeIndex;
import pk.ansi4j.css.api.text.TextAttributeValue;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultTextAttributeConfig implements TextAttributeConfig {

    public static class Builder {

        private AttributeValue defaultForegroundColor;

        private int fgIndex;

        private PaletteType fgPaletteType;

        private AttributeValue defaultBackgroundColor;

        private int bgIndex;

        private PaletteType bgPaletteType;

        private List<String> fontFamilies;

        private boolean extraColorsEnabled = false;

        private Palette8 palette8;

        private Palette16 palette16;

        private Palette256 palette256;

        public Builder() {

        }

        /**
         * See {@link TextAttributeConfig#getDefaultForegroundColor() }.
         *
         * Use this method, if color is not taken from any palette.
         *
         * @param defaultForegroundColor
         * @return
         */
        public Builder defaultForegroundColor(int defaultForegroundColor) {
            this.defaultForegroundColor = new DefaultAttributeValue(null, defaultForegroundColor);
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getDefaultBackgroundColor() }.
         *
         * Use this method, if color is not taken from any palette.
         *
         * @param defaultBackgroundColor
         * @return
         */
        public Builder defaultBackgroundColor(int defaultBackgroundColor) {
            this.defaultBackgroundColor = new DefaultAttributeValue(null, defaultBackgroundColor);
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getDefaultForegroundColor() }.
         *
         * Use this method, if color is taken from some palette.
         *
         * @return
         */
        public Builder defaultForegroundColor(int index, PaletteType paletteType) {
            this.fgIndex = index;
            this.fgPaletteType = paletteType;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getDefaultBackgroundColor() }.
         *
         * Use this method, if color is taken from some palette.
         *
         * @return
         */
        public Builder defaultBackgroundColor(int index, PaletteType paletteType) {
            this.bgIndex = index;
            this.bgPaletteType = paletteType;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getFontFamilies() }.
         *
         * @param fontFamilies
         * @return
         */
        public Builder fontFamilies(List<String> fontFamilies) {
            this.fontFamilies = fontFamilies;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#areExtraColorsEnabled() }.
         *
         * @param enabled
         * @return
         */
        public Builder extraColorsEnabled(boolean enabled) {
            this.extraColorsEnabled = enabled;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getPalette8() }.
         * @param palette8
         * @return
         */
        public Builder palette8(Palette8 palette8) {
            this.palette8 = palette8;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getPalette16() }.
         * @param palette16
         * @return
         */
        public Builder palette16(Palette16 palette16) {
            this.palette16 = palette16;
            return this;
        }

        /**
         * See {@link TextAttributeConfig#getPalette256() }.
         * @param palette256
         * @return
         */
        public Builder palette256(Palette256 palette256) {
            this.palette256 = palette256;
            return this;
        }

        /**
         * Builds configuration.
         * @return
         */
        public TextAttributeConfig build() {
            this.resolvePaletteColors();
            this.validate();
            var config = new DefaultTextAttributeConfig(this);
            return config;
        }

        private void resolvePaletteColors() {
            if (fgPaletteType != null) {
                var color = this.resolveColor(fgIndex, fgPaletteType);
                this.defaultForegroundColor = new DefaultAttributeValue(fgIndex, color, fgPaletteType);
            }
            if (bgPaletteType != null) {
                var color = this.resolveColor(bgIndex, bgPaletteType);
                this.defaultBackgroundColor = new DefaultAttributeValue(bgIndex, color, bgPaletteType);
            }
        }

        private int resolveColor(int index, PaletteType paletteType) {
            switch (paletteType) {
                case PALETTE_8: return this.palette8.getColors()[index];
                case PALETTE_16: return this.palette16.getColors()[index];
                case PALETTE_256: return this.palette256.getColors()[index];
                default:
                    throw new IllegalArgumentException("Unknown palette type=" + paletteType);
            }
        }

        private void validate() {
            if (this.defaultForegroundColor == null) {
                throw new IllegalStateException("No foreground default color");
            }
            if (this.defaultBackgroundColor == null) {
                throw new IllegalStateException("No background default color");
            }
            if (this.fontFamilies == null || this.fontFamilies.isEmpty()) {
                throw new IllegalStateException("No font families, provide at least 1");
            }
            if (this.extraColorsEnabled) {
                if (this.palette16 == null || this.palette256 == null) {
                    throw new IllegalStateException("No palette16 or palette 256, but extra colors are enabled");
                }
            } else {
                if (this.palette8 == null) {
                    throw new IllegalStateException("No palette8, but extra colors are not enabled");
                }
            }
        }
    }

    private final AttributeValue defaultForegroundColor;

    private final AttributeValue defaultBackgroundColor;

    private final List<String> fontFamilies;

    private final boolean extraColorsEnabled;

    private final Palette8 palette8;

    private final Palette16 palette16;

    private final Palette256 palette256;

    private final Map<Attribute, AttributeValue> defaultValuesByAttribute = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getDefaultForegroundColor() {
        return this.defaultForegroundColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getDefaultBackgroundColor() {
        return this.defaultBackgroundColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getFontFamilies() {
        return this.fontFamilies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areExtraColorsEnabled() {
        return this.extraColorsEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Palette16 getPalette16() {
        return this.palette16;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Palette256 getPalette256() {
        return this.palette256;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Palette8 getPalette8() {
        return this.palette8;
    }

    @Override
    public Class<? extends Attribute> getTargetAttribute() {
        return TextAttribute.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getDefaultValue(Attribute attribute) {
        return this.defaultValuesByAttribute.get(attribute);
    }

    private DefaultTextAttributeConfig(Builder builder) {
        this.defaultForegroundColor = builder.defaultForegroundColor;
        this.defaultBackgroundColor = builder.defaultBackgroundColor;
        this.fontFamilies = Collections.unmodifiableList(new ArrayList<>(builder.fontFamilies));
        this.extraColorsEnabled = builder.extraColorsEnabled;
        this.palette8 = builder.palette8;
        this.palette16 = builder.palette16;
        this.palette256 = builder.palette256;
        this.initAttributeDefaultValues();
    }

    private void initAttributeDefaultValues() {
        var m = this.defaultValuesByAttribute;

        m.put(TextAttribute.WEIGHT, new DefaultAttributeValue(null, TextAttributeValue.WEIGHT_NORMAL));
        m.put(TextAttribute.ITALIC, new DefaultAttributeValue(null, TextAttributeValue.ITALIC_OFF));

        m.put(TextAttribute.UNDERLINE, new DefaultAttributeValue(null, TextAttributeValue.UNDERLINE_OFF));
        m.put(TextAttribute.BLINKING, new DefaultAttributeValue(null, TextAttributeValue.BLINKING_OFF));
        m.put(TextAttribute.REVERSE_VIDEO, new DefaultAttributeValue(null, TextAttributeValue.REVERSE_VIDEO_OFF));
        m.put(TextAttribute.VISIBILITY, new DefaultAttributeValue(null, TextAttributeValue.VISIBILITY_VISIBLE));
        m.put(TextAttribute.STRIKETHROUGH, new DefaultAttributeValue(null, TextAttributeValue.STRIKETHROUGH_OFF));

        m.put(TextAttribute.FONT, new DefaultAttributeValue(TextAttributeIndex.FONT_DEFAULT,
                this.fontFamilies.get(TextAttributeIndex.FONT_DEFAULT)));
        m.put(TextAttribute.FOREGROUND_COLOR, this.defaultForegroundColor);
        m.put(TextAttribute.BACKGROUND_COLOR, this.defaultBackgroundColor);
    }
}
