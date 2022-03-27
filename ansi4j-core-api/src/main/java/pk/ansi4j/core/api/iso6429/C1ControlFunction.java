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
package pk.ansi4j.core.api.iso6429;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pk.ansi4j.core.api.utils.Characters;

/**
 * C1 set functions. This file was created on the base of the fifth edition of ECMA-48. All descriptions of
 * the functions (that are used as Java documentation comments in this file) were taken from this ECMA-48 standard.
 *
 * @author Pavel Kastornyy
 */
public enum C1ControlFunction implements ControlFunction {

    /**
     * Notation: (C1)
     * Representation: 09/15=0x9f='' or ESC 05/15=0x5f='_'
     * APC is used as the opening delimiter of a control string for application program use. The command
     * string following may consist of bit combinations in the range 00/08 to 00/13 and 02/00 to 07/14. The
     * control string is closed by the terminating delimiter STRING TERMINATOR (ST). The interpretation of
     * the command string depends on the relevant application program.
     */
    APC_APPLICATION_PROGRAM_COMMAND(Characters.ESC + "_", 0x9f),

    /**
     * Notation: (C1)
     * Representation: 08/02=0x82='' or ESC 04/02=0x42='B'
     * BPH is used to indicate a point where a line break may occur when text is formatted. BPH may occur
     * between two graphic characters, either or both of which may be SPACE.
     */
    BPH_BREAK_PERMITTED_HERE(Characters.ESC + "B", 0x82),

    /**
     * Notation: (C1)
     * Representation: 09/04=0x94='' or ESC 05/04=0x54='T'
     * CCH is used to indicate that both the preceding graphic character in the data stream, (represented by one
     * or more bit combinations) including SPACE, and the control function CCH itself are to be ignored for
     * further interpretation of the data stream.
     * If the character preceding CCH in the data stream is a control function (represented by one or more bit
     * combinations), the effect of CCH is not defined by this Standard.
     */
    CCH_CANCEL_CHARACTER(Characters.ESC + "T", 0x94),

    /**
     * Notation: (C1)
     * Representation: 09/11=0x9b='' or ESC 05/11=0x5b='['
     * CSI is used as the first character of a control sequence, see 5.4.
     */
    CSI_CONTROL_SEQUENCE_INTRODUCER(Characters.ESC + "[", 0x9b),

    /**
     * Notation: (C1)
     * Representation: 09/00=0x90='' or ESC 05/00=0x50='P'
     * DCS is used as the opening delimiter of a control string for device control use. The command string
     * following may consist of bit combinations in the range 00/08 to 00/13 and 02/00 to 07/14. The control
     * string is closed by the terminating delimiter STRING TERMINATOR (ST).
     * The command string represents either one or more commands for the receiving device, or one or more
     * status reports from the sending device. The purpose and the format of the command string are specified
     * by the most recent occurrence of IDENTIFY DEVICE CONTROL STRING (IDCS), if any, or depend on
     * the sending and/or the receiving device.
     */
    DCS_DEVICE_CONTROL_STRING(Characters.ESC + "P", 0x90),

    /**
     * Notation: (C1)
     * Representation: 09/07=0x97='' or ESC 05/07=0x57='W'
     * EPA is used to indicate that the active presentation position is the last of a string of character positions
     * in the presentation component, the contents of which are protected against manual alteration, are
     * guarded against transmission or transfer, depending on the setting of the GUARDED AREA TRANSFER
     * MODE (GATM), and may be protected against erasure, depending on the setting of the ERASURE
     * MODE (ERM). The beginning of this string is indicated by START OF GUARDED AREA (SPA).
     * NOTE
     * The control functions for area definition (DAQ, EPA, ESA, SPA, SSA) should not be used within an SRS
     * string or an SDS string.
     */
    EPA_END_OF_GUARDED_AREA(Characters.ESC + "W", 0x97),

    /**
     * Notation: (C1)
     * Representation: 08/07=0x87='' or ESC 04/07=0x47='G'
     * ESA is used to indicate that the active presentation position is the last of a string of character positions
     * in the presentation component, the contents of which are eligible to be transmitted in the form of a data
     * stream or transferred to an auxiliary input/output device. The beginning of this string is indicated by
     * START OF SELECTED AREA (SSA).
     * NOTE
     * The control function for area definition (DAQ, EPA, ESA, SPA, SSA) should not be used within an SRS
     * string or an SDS string.
     */
    ESA_END_OF_SELECTED_AREA(Characters.ESC + "G", 0x87),

    /**
     * Notation: (C1)
     * Representation: 08/09=0x89='' or ESC 04/09=0x49='I'
     * HTJ causes the contents of the active field (the field in the presentation component that contains the
     * active presentation position) to be shifted forward so that it ends at the character position preceding the
     * following character tabulation stop. The active presentation position is moved to that following character
     * tabulation stop. The character positions which precede the beginning of the shifted string are put into the
     * erased state.
     */
    HTJ_CHARACTER_TABULATION_WITH_JUSTIFICATION(Characters.ESC + "I", 0x89),

    /**
     * Notation: (C1)
     * Representation: 08/08=0x88='' or ESC 04/08=0x48='H'
     * HTS causes a character tabulation stop to be set at the active presentation position in the presentation
     * component.
     * The number of lines affected depends on the setting of the TABULATION STOP MODE (TSM).
     */
    HTS_CHARACTER_TABULATION_SET(Characters.ESC + "H", 0x88),

    /**
     * Notation: (C1)
     * Representation: 09/05=0x95='' or ESC 05/05=0x55='U'
     * MW is used to set a message waiting indicator in the receiving device. An appropriate acknowledgement
     * to the receipt of MW may be given by using DEVICE STATUS REPORT (DSR).
     */
    MW_MESSAGE_WAITING(Characters.ESC + "U", 0x95),

    /**
     * Notation: (C1)
     * Representation: 08/03=0x83='' or ESC 04/03=0x43='C'
     * NBH is used to indicate a point where a line break shall not occur when text is formatted. NBH may
     * occur between two graphic characters either or both of which may be SPACE.
     */
    NBH_NO_BREAK_HERE(Characters.ESC + "C", 0x83),

    /**
     * Notation: (C1)
     * Representation: 08/05=0x85='' or ESC 04/05=0x45='E'
     * The effect of NEL depends on the setting of the DEVICE COMPONENT SELECT MODE (DCSM) and
     * on the parameter value of SELECT IMPLICIT MOVEMENT DIRECTION (SIMD).
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to PRESENTATION and with a
     * parameter value of SIMD equal to 0, NEL causes the active presentation position to be moved to the line
     * home position of the following line in the presentation component. The line home position is established
     * by the parameter value of SET LINE HOME (SLH).
     * With a parameter value of SIMD equal to 1, NEL causes the active presentation position to be moved to
     * the line limit position of the following line in the presentation component. The line limit position is
     * established by the parameter value of SET LINE LIMIT (SLL).
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to DATA and with a parameter value of
     * SIMD equal to 0, NEL causes the active data position to be moved to the line home position of the
     * following line in the data component. The line home position is established by the parameter value of
     * SET LINE HOME (SLH).
     * With a parameter value of SIMD equal to 1, NEL causes the active data position to be moved to the line
     * limit position of the following line in the data component. The line limit position is established by the
     * parameter value of SET LINE LIMIT (SLL).
     */
    NEL_NEXT_LINE(Characters.ESC + "E", 0x85),

    /**
     * Notation: (C1)
     * Representation: 09/13=0x9d='' or ESC 05/13=0x5d=']'
     * OSC is used as the opening delimiter of a control string for operating system use. The command string
     * following may consist of a sequence of bit combinations in the range 00/08 to 00/13 and 02/00 to 07/14.
     * The control string is closed by the terminating delimiter STRING TERMINATOR (ST). The
     * interpretation of the command string depends on the relevant operating system.
     */
    OSC_OPERATING_SYSTEM_COMMAND(Characters.ESC + "]", 0x9d),

    /**
     * Notation: (C1)
     * Representation: 08/11=0x8b='' or ESC 04/11=0x4b='K'
     * PLD causes the active presentation position to be moved in the presentation component to the
     * corresponding position of an imaginary line with a partial offset in the direction of the line progression.
     * This offset should be sufficient either to image following characters as subscripts until the first
     * following occurrence of PARTIAL LINE BACKWARD (PLU) in the data stream, or, if preceding
     * characters were imaged as superscripts, to restore imaging of following characters to the active line (the
     * line that contains the active presentation position).
     * Any interactions between PLD and format effectors other than PLU are not defined by this Standard.
     */
    PLD_PARTIAL_LINE_FORWARD(Characters.ESC + "K", 0x8b),

    /**
     * Notation: (C1)
     * Representation: 08/12=0x8c='' or ESC 04/12=0x4c='L'
     * PLU causes the active presentation position to be moved in the presentation component to the
     * corresponding position of an imaginary line with a partial offset in the direction opposite to that of the
     * line progression. This offset should be sufficient either to image following characters as superscripts
     * until the first following occurrence of PARTIAL LINE FORWARD (PLD) in the data stream, or, if
     * preceding characters were imaged as subscripts, to restore imaging of following characters to the active
     * line (the line that contains the active presentation position).
     * Any interactions between PLU and format effectors other than PLD are not defined by this Standard.
     */
    PLU_PARTIAL_LINE_BACKWARD(Characters.ESC + "L", 0x8c),

    /**
     * Notation: (C1)
     * Representation: 09/14=0x9e='' or ESC 05/14=0x5e='^'
     * PM is used as the opening delimiter of a control string for privacy message use. The command string
     * following may consist of a sequence of bit combinations in the range 00/08 to 00/13 and 02/00 to 07/14.
     * The control string is closed by the terminating delimiter STRING TERMINATOR (ST). The
     * interpretation of the command string depends on the relevant privacy discipline.
     */
    PM_PRIVACY_MESSAGE(Characters.ESC + "^", 0x9e),

    /**
     * Notation: (C1)
     * Representation: 09/01=0x91='' or ESC 05/01=0x51='Q'
     * PU1 is reserved for a function without standardized meaning for private use as required, subject to the
     * prior agreement between the sender and the recipient of the data.
     */
    PU1_PRIVATE_USE_ONE(Characters.ESC + "Q", 0x91),

    /**
     * Notation: (C1)
     * Representation: 09/02=0x92='' or ESC 05/02=0x52='R'
     * PU2 is reserved for a function without standardized meaning for private use as required, subject to the
     * prior agreement between the sender and the recipient of the data.
     */
    PU2_PRIVATE_USE_TWO(Characters.ESC + "R", 0x92),

    /**
     * Notation: (C1)
     * Representation: 08/13=0x8d='' or ESC 04/13=0x4d='M'
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to PRESENTATION, RI causes the
     * active presentation position to be moved in the presentation component to the corresponding character
     * position of the preceding line.
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to DATA, RI causes the active data
     * position to be moved in the data component to the corresponding character position of the preceding
     * line.
     */
    RI_REVERSE_LINE_FEED(Characters.ESC + "M", 0x8d),

    /**
     * Notation: (C1)
     * Representation: 09/10=0x9a='' or ESC 05/10=0x5a='Z'
     * SCI and the bit combination following it are used to represent a control function or a graphic character.
     * The bit combination following SCI must be from 00/08 to 00/13 or 02/00 to 07/14. The use of SCI is
     * reserved for future standardization.
     */
    SCI_SINGLE_CHARACTER_INTRODUCER(Characters.ESC + "Z", 0x9a),

    /**
     * Notation: (C1)
     * Representation: 09/08=0x98='' or ESC 05/08=0x58='X'
     * SOS is used as the opening delimiter of a control string. The character string following may consist of
     * any bit combination, except those representing SOS or STRING TERMINATOR (ST). The control string
     * is closed by the terminating delimiter STRING TERMINATOR (ST). The interpretation of the character
     * string depends on the application.
     */
    SOS_START_OF_STRING(Characters.ESC + "X", 0x98),

    /**
     * Notation: (C1)
     * Representation: 09/06=0x96='' or ESC 05/06=0x56='V'
     * SPA is used to indicate that the active presentation position is the first of a string of character positions
     * in the presentation component, the contents of which are protected against manual alteration, are
     * guarded against transmission or transfer, depending on the setting of the GUARDED AREA TRANSFER
     * MODE (GATM) and may be protected against erasure, depending on the setting of the ERASURE
     * MODE (ERM). The end of this string is indicated by END OF GUARDED AREA (EPA).
     * NOTE
     * The control functions for area definition (DAQ, EPA, ESA, SPA, SSA) should not be used within an SRS
     * string or an SDS string.
     */
    SPA_START_OF_GUARDED_AREA(Characters.ESC + "V", 0x96),

    /**
     * Notation: (C1)
     * Representation: 08/06=0x86='' or ESC 04/06=0x46='F'
     * SSA is used to indicate that the active presentation position is the first of a string of character positions
     * in the presentation component, the contents of which are eligible to be transmitted in the form of a data
     * stream or transferred to an auxiliary input/output device.
     * The end of this string is indicated by END OF SELECTED AREA (ESA). The string of characters
     * actually transmitted or transferred depends on the setting of the GUARDED AREA TRANSFER MODE
     * (GATM) and on any guarded areas established by DEFINE AREA QUALIFICATION (DAQ), or by
     * START OF GUARDED AREA (SPA) and END OF GUARDED AREA (EPA).
     * NOTE
     * The control functions for area definition (DAQ, EPA, ESA, SPA, SSA) should not be used within an SRS
     * string or an SDS string.
     */
    SSA_START_OF_SELECTED_AREA(Characters.ESC + "F", 0x86),

    /**
     * Notation: (C1)
     * Representation: 08/14=0x8e='' or ESC 04/14=0x4e='N'
     * SS2 is used for code extension purposes. It causes the meanings of the bit combinations following it in
     * the data stream to be changed.
     * The use of SS2 is defined in Standard ECMA-35.
     */
    SS2_SINGLE_SHIFT_TWO(Characters.ESC + "N", 0x8e),

    /**
     * Notation: (C1)
     * Representation: 08/15=0x8f='' or ESC 04/15=0x4f='O'
     * SS3 is used for code extension purposes. It causes the meanings of the bit combinations following it in
     * the data stream to be changed.
     * The use of SS3 is defined in Standard ECMA-35.
     */
    SS3_SINGLE_SHIFT_THREE(Characters.ESC + "O", 0x8f),

    /**
     * Notation: (C1)
     * Representation: 09/12=0x9c='' or ESC 05/12=0x5c='\'
     * ST is used as the closing delimiter of a control string opened by APPLICATION PROGRAM
     * COMMAND (APC), DEVICE CONTROL STRING (DCS), OPERATING SYSTEM COMMAND
     * (OSC), PRIVACY MESSAGE (PM), or START OF STRING (SOS).
     */
    ST_STRING_TERMINATOR(Characters.ESC + "\\", 0x9c),

    /**
     * Notation: (C1)
     * Representation: 09/03=0x93='' or ESC 05/03=0x53='S'
     * STS is used to establish the transmit state in the receiving device. In this state the transmission of data
     * from the device is possible.
     * The actual initiation of transmission of data is performed by a data communication or input/output
     * interface control procedure which is outside the scope of this Standard.
     * The transmit state is established either by STS appearing in the received data stream or by the operation
     * of an appropriate key on a keyboard.
     */
    STS_SET_TRANSMIT_STATE(Characters.ESC + "S", 0x93),

    /**
     * Notation: (C1)
     * Representation: 08/10=0x8a='' or ESC 04/10=0x4a='J'
     * VTS causes a line tabulation stop to be set at the active line (the line that contains the active presentation
     * position).
     */
    VTS_LINE_TABULATION_SET(Characters.ESC + "J", 0x8a);

    /**
     * Returns C1 functions which are opening delimiters for functions of control string type.
     * @return
     */
    public static Set<C1ControlFunction> getControlStringOpeningDelimiters() {
        return Set.of(
                APC_APPLICATION_PROGRAM_COMMAND,
                DCS_DEVICE_CONTROL_STRING,
                OSC_OPERATING_SYSTEM_COMMAND,
                PM_PRIVACY_MESSAGE,
                SOS_START_OF_STRING);
    }

    private final String pattern;

    /**
     * This pattern is used in 8 bit environment.
     */
    private final String pattern8Bit;

    private C1ControlFunction(String pattern, int pattern8Bit) {
        this.pattern = pattern;
        char c = (char) pattern8Bit;
        this.pattern8Bit = String.valueOf(c);
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public ControlFunctionType getType() {
        return ControlFunctionType.C1_SET;
    }

    @Override
    public List<Object> getDefaultValues() {
        return null;
    }

    public String get8BitPattern() {
        return pattern8Bit;
    }
}

