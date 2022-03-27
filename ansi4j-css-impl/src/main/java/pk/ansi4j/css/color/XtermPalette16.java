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
package pk.ansi4j.css.color;

import pk.ansi4j.css.api.color.Palette16;
import pk.ansi4j.css.api.color.Palette8;

/**
 * See https://en.wikipedia.org/wiki/ANSI_escape_code#3-bit_and_4-bit
 *
 * @author Pavel Kastornyy
 */
public class XtermPalette16 implements Palette16 {

    private static final int[] COLORS = {

            0x000000, 0xcd0000, 0x00cd00, 0xcdcd00, 0x0000ee, 0xcd00cd, 0x00cdcd, 0xe5e5e5,
            0x7f7f7f, 0xff0000, 0x00ff00, 0xffff00, 0x5c5cff, 0xff00ff, 0x00ffff, 0xffffff

    };

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getColors() {
        return COLORS;
    }

}
