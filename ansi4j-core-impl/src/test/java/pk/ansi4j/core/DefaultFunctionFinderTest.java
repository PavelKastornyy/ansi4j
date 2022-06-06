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
package pk.ansi4j.core;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
import pk.ansi4j.core.api.Environment;
import pk.ansi4j.core.api.iso6429.C0ControlFunction;
import pk.ansi4j.core.api.iso6429.C1ControlFunction;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.api.iso6429.IndependentControlFunction;
import pk.ansi4j.core.api.utils.Characters;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultFunctionFinderTest {

    private static DefaultFunctionFinder finder7Bit;

    private static DefaultFunctionFinder finder8Bit;

    @BeforeAll
    public static void init() {
        finder7Bit = new DefaultFunctionFinder();
        finder7Bit.initialize(Environment._7_BIT);

        finder8Bit = new DefaultFunctionFinder();
        finder8Bit.initialize(Environment._8_BIT);
    }

    /* 7 BIT */

    @Test
    public void find_c0Set7BitEsc_success() {
        var result = finder7Bit.find(0, "" + Characters.ESC).get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C0_SET));
        assertThat(result.getFunction(), equalTo(C0ControlFunction.ESC_ESCAPE));
    }

    @Test
    public void find_c0Set7BitVt_success() {
        var result = finder7Bit.find(0, "\u000B").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C0_SET));
        assertThat(result.getFunction(), equalTo(C0ControlFunction.VT_LINE_TABULATION));
    }

    @Test
    public void find_c1Set7BitNel_success() {
        var result = finder7Bit.find(0, Characters.ESC + "E").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C1_SET));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.NEL_NEXT_LINE));
    }

    @Test
    public void find_c1Set7BitCsi_success() {
        var result = finder7Bit.find(0, Characters.ESC + "[").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_SEQUENCE));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.CSI_CONTROL_SEQUENCE_INTRODUCER));
    }

    @Test
    public void find_independent7BitCmd_success() {
        var result = finder7Bit.find(0, "abc" + Characters.ESC + "d").get();
        assertThat(result.getFunctionIndex(), equalTo(3));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.INDEPENDENT_FUNCTION));
        assertThat(result.getFunction(), equalTo(IndependentControlFunction.CMD_CODING_METHOD_DELIMITER));
    }

    @Test
    public void find_controlSequence7Bit_success() {
        var result = finder7Bit.find(2, "ab123" + Characters.ESC + "[20m").get();
        assertThat(result.getFunctionIndex(), equalTo(5));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_SEQUENCE));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.CSI_CONTROL_SEQUENCE_INTRODUCER));
    }

    @Test
    public void find_controlString7Bit_success() {
        var result = finder7Bit.find(0, Characters.ESC + "^").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_STRING));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.PM_PRIVACY_MESSAGE));
    }

    /* 8 BIT */

    @Test
    public void find_c0Set8BitEsc_success() {
        var result = finder8Bit.find(0, "" + Characters.ESC).get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C0_SET));
        assertThat(result.getFunction(), equalTo(C0ControlFunction.ESC_ESCAPE));
    }

    @Test
    public void find_c0Set8BitVt_success() {
        var result = finder8Bit.find(0, "\u000B").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C0_SET));
        assertThat(result.getFunction(), equalTo(C0ControlFunction.VT_LINE_TABULATION));
    }

    @Test
    public void find_c1Set8BitNel_success() {
        var result = finder8Bit.find(0, "\u0085").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.C1_SET));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.NEL_NEXT_LINE));
    }

    @Test
    public void find_c1Set8BitCsi_success() {
        var result = finder8Bit.find(0, "\u009b").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_SEQUENCE));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.CSI_CONTROL_SEQUENCE_INTRODUCER));
    }

    @Test
    public void find_independent8BitCmd_success() {
        var result = finder8Bit.find(0, "abc" + Characters.ESC + "d").get();
        assertThat(result.getFunctionIndex(), equalTo(3));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.INDEPENDENT_FUNCTION));
        assertThat(result.getFunction(), equalTo(IndependentControlFunction.CMD_CODING_METHOD_DELIMITER));
    }

    @Test
    public void find_controlSequence8Bit_success() {
        var result = finder8Bit.find(2, "ab123\u009b20m").get();
        assertThat(result.getFunctionIndex(), equalTo(5));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_SEQUENCE));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.CSI_CONTROL_SEQUENCE_INTRODUCER));
    }

    @Test
    public void find_controlString8Bit_success() {
        var result = finder8Bit.find(0, "\u009e").get();
        assertThat(result.getFunctionIndex(), equalTo(0));
        assertThat(result.getFunctionType(), equalTo(ControlFunctionType.CONTROL_STRING));
        assertThat(result.getFunction(), equalTo(C1ControlFunction.PM_PRIVACY_MESSAGE));
    }

}
