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

/**
 * See https://en.wikipedia.org/wiki/ANSI_escape_code#3-bit_and_4-bit
 *
 * @author Pavel Kastornyy
 */
public class VgaPalette16  implements Palette16 {

    private static final int[] COLORS = {

            0x000000, 0xaa0000, 0x00aa00, 0xaa5500, 0x0000aa, 0xaa00aa, 0x00aaaa, 0xaaaaaa,
            0x555555, 0xff5555, 0x55ff55, 0xffff55, 0x5555ff, 0xff55ff, 0x55ffff, 0xffffff

    };

    private int[] colors = COLORS.clone();

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getColors() {
        return colors;
    }

}
