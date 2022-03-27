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
package pk.ansi4j.css.api.text;

import java.util.List;
import pk.ansi4j.css.api.AttributeConfig;
import pk.ansi4j.css.api.AttributeValue;
import pk.ansi4j.css.api.color.Palette16;
import pk.ansi4j.css.api.color.Palette256;
import pk.ansi4j.css.api.color.Palette8;

/**
 *
 * @author Pavel Kastornyy
 */
public interface TextAttributeConfig extends AttributeConfig {

    /**
     * Default foreground color (0xXXXXXX).
     *
     * @return
     */
    AttributeValue getDefaultForegroundColor();

    /**
     * Default background color (0xXXXXXX).
     *
     * @return
     */
    AttributeValue getDefaultBackgroundColor();

    /**
     * Default font has index 0, alternative_1 index 1, etc.
     *
     * @return
     */
    List<String> getFontFamilies();

    /**
     * ISO 6429 supports only 8 colors (3 bits). However, today many terminals supports 4, 8 and 24 bit colors.
     * This flag is used to enable support of these extra colors.
     *
     * @return
     */
    boolean areExtraColorsEnabled();

    /**
     * Returns palette that is used if extra colors ARE NOT enabled.
     *
     * @return
     */
    Palette8 getPalette8();

    /**
     * Returns 16 color palette that is used if extra colors ARE enabled.
     *
     * @return
     */
    Palette16 getPalette16();

    /**
     * Returns 256 palette that is used if extra colors ARE enabled.
     *
     * @return
     */
    Palette256 getPalette256();

}
