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

import pk.ansi4j.core.api.Environment;
import java.util.List;

/**
 * C0 set functions. This file was created on the base of the fifth edition of ECMA-48. All descriptions of
 * the functions (that are used as Java documentation comments in this file) were taken from this ECMA-48 standard.
 *
 * @author Pavel Kastornyy
 */
public enum C0ControlFunction implements ControlFunction {

    /**
     * Notation: (C0)
     * Representation: 00/06=0x6=''
     * ACK is transmitted by a receiver as an affirmative response to the sender.
     * The use of ACK is defined in ISO 1745.
     */
    ACK_ACKNOWLEDGE(0x06),

    /**
     * Notation: (C0)
     * Representation: 00/07=0x7=''
     * BEL is used when there is a need to call for attention; it may control alarm or attention devices.
     */
    BEL_BELL(0x07),

    /**
     * Notation: (C0)
     * Representation: 00/08=0x8=''
     * BS causes the active data position to be moved one character position in the data component in the
     * direction opposite to that of the implicit movement.
     * The direction of the implicit movement depends on the parameter value of SELECT IMPLICIT
     * MOVEMENT DIRECTION (SIMD).
     */
    BS_BACKSPACE(0x08),

    /**
     * Notation: (C0)
     * Representation: 01/08=0x18=''
     * CAN is used to indicate that the data preceding it in the data stream is in error. As a result, this data
     * shall be ignored. The specific meaning of this control function shall be defined for each application
     * and/or between sender and recipient.
     */
    CAN_CANCEL(0x18),

    /**
     * Notation: (C0)
     * Representation: 00/13=0xd=''
     * The effect of CR depends on the setting of the DEVICE COMPONENT SELECT MODE (DCSM) and
     * on the parameter value of SELECT IMPLICIT MOVEMENT DIRECTION (SIMD).
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to PRESENTATION and with the
     * parameter value of SIMD equal to 0, CR causes the active presentation position to be moved to the line
     * home position of the same line in the presentation component. The line home position is established by
     * the parameter value of SET LINE HOME (SLH).
     * With a parameter value of SIMD equal to 1, CR causes the active presentation position to be moved to
     * the line limit position of the same line in the presentation component. The line limit position is
     * established by the parameter value of SET LINE LIMIT (SLL).
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to DATA and with a parameter value of
     * SIMD equal to 0, CR causes the active data position to be moved to the line home position of the same
     * line in the data component. The line home position is established by the parameter value of SET LINE
     * HOME (SLH).
     * With a parameter value of SIMD equal to 1, CR causes the active data position to be moved to the line
     * limit position of the same line in the data component. The line limit position is established by the
     * parameter value of SET LINE LIMIT (SLL).
     */
    CR_CARRIAGE_RETURN(0x0d),

    /**
     * Notation: (C0)
     * Representation: 01/01=0x11=''
     * DC1 is primarily intended for turning on or starting an ancillary device. If it is not required for this
     * purpose, it may be used to restore a device to the basic mode of operation (see also DC2 and DC3), or
     * any other device control function not provided by other DCs.
     * NOTE
     * When used for data flow control, DC1 is sometimes called "X-ON".
     */
    DC1_DEVICE_CONTROL_ONE(0x11),

    /**
     * Notation: (C0)
     * Representation: 01/02=0x12=''
     * DC2 is primarily intended for turning on or starting an ancillary device. If it is not required for this
     * purpose, it may be used to set a device to a special mode of operation (in which case DC1 is used to
     * restore the device to the basic mode), or for any other device control function not provided by other
     * DCs.
     */
    DC2_DEVICE_CONTROL_TWO(0x12),

    /**
     * Notation: (C0)
     * Representation: 01/03=0x13=''
     * DC3 is primarily intended for turning off or stopping an ancillary device. This function may be a
     * secondary level stop, for example wait, pause, stand-by or halt (in which case DC1 is used to restore
     * normal operation). If it is not required for this purpose, it may be used for any other device control
     * function not provided by other DCs.
     * NOTE
     * When used for data flow control, DC3 is sometimes called "X-OFF".
     */
    DC3_DEVICE_CONTROL_THREE(0x13),

    /**
     * Notation: (C0)
     * Representation: 01/04=0x14=''
     * DC4 is primarily intended for turning off, stopping or interrupting an ancillary device. If it is not
     * required for this purpose, it may be used for any other device control function not provided by other
     * DCs.
     */
    DC4_DEVICE_CONTROL_FOUR(0x14),

    /**
     * Notation: (C0)
     * Representation: 01/00=0x10=''
     * DLE is used exclusively to provide supplementary transmission control functions.
     * The use of DLE is defined in ISO 1745.
     */
    DLE_DATA_LINK_ESCAPE(0x10),

    /**
     * Notation: (C0)
     * Representation: 01/09=0x19=''
     * EM is used to identify the physical end of a medium, or the end of the used portion of a medium, or the
     * end of the wanted portion of data recorded on a medium.
     */
    EM_END_OF_MEDIUM(0x19),

    /**
     * Notation: (C0)
     * Representation: 00/05=0x5=''
     * ENQ is transmitted by a sender as a request for a response from a receiver.
     * The use of ENQ is defined in ISO 1745.
     */
    ENQ_ENQUIRY(0x05),

    /**
     * Notation: (C0)
     * Representation: 00/04=0x4=''
     * EOT is used to indicate the conclusion of the transmission of one or more texts.
     * The use of EOT is defined in ISO 1745.
     */
    EOT_END_OF_TRANSMISSION(0x04),

    /**
     * Notation: (C0)
     * Representation: 01/11=0x1b=''
     * ESC is used for code extension purposes. It causes the meanings of a limited number of bit combinations
     * following it in the data stream to be changed.
     * The use of ESC is defined in Standard ECMA-35.
     */
    ESC_ESCAPE(0x1b),

    /**
     * Notation: (C0)
     * Representation: 01/07=0x17=''
     * ETB is used to indicate the end of a block of data where the data are divided into such blocks for
     * transmission purposes.
     * The use of ETB is defined in ISO 1745.
     */
    ETB_END_OF_TRANSMISSION_BLOCK(0x17),

    /**
     * Notation: (C0)
     * Representation: 00/03=0x3=''
     * ETX is used to indicate the end of a text.
     * The use of ETX is defined in ISO 1745.
     */
    ETX_END_OF_TEXT(0x03),

    /**
     * Notation: (C0)
     * Representation: 00/12=0xc=''
     * FF causes the active presentation position to be moved to the corresponding character position of the
     * line at the page home position of the next form or page in the presentation component. The page home
     * position is established by the parameter value of SET PAGE HOME (SPH).
     */
    FF_FORM_FEED(0x0c),

    /**
     * Notation: (C0)
     * Representation: 00/09=0x9='	'
     * HT causes the active presentation position to be moved to the following character tabulation stop in the
     * presentation component.
     * In addition, if that following character tabulation stop has been set by TABULATION ALIGN CENTRE
     * (TAC), TABULATION ALIGN LEADING EDGE (TALE), TABULATION ALIGN TRAILING EDGE
     * (TATE) or TABULATION CENTRED ON CHARACTER (TCC), HT indicates the beginning of a string
     * of text which is to be positioned within a line according to the properties of that tabulation stop. The end
     * of the string is indicated by the next occurrence of HT or CARRIAGE RETURN (CR) or NEXT LINE
     * (NEL) in the data stream.
     */
    HT_CHARACTER_TABULATION(0x09),

    /**
     * Notation: (C0)
     * Representation: 01/15=0x1f=''
     * Specific name: US - UNIT SEPARATOR
     * IS1 is used to separate and qualify data logically; its specific meaning has to be defined for each
     * application. If this control function is used in hierarchical order, it may delimit a data item called a unit,
     * see 8.2.10.
     */
    IS1_INFORMATION_SEPARATOR_ONE(0x1f),

    /**
     * Notation: (C0)
     * Representation: 01/14=0x1e=''
     * Specific name: RS - RECORD SEPARATOR
     * IS2 is used to separate and qualify data logically; its specific meaning has to be defined for each
     * application. If this control function is used in hierarchical order, it may delimit a data item called a
     * record, see 8.2.10.
     */
    IS2_INFORMATION_SEPARATOR_TWO(0x1e),

    /**
     * Notation: (C0)
     * Representation: 01/13=0x1d=''
     * Specific name: GS - GROUP SEPARATOR
     * IS3 is used to separate and qualify data logically; its specific meaning has to be defined for each
     * application. If this control function is used in hierarchical order, it may delimit a data item called a
     * group, see 8.2.10.
     */
    IS3_INFORMATION_SEPARATOR_THREE(0x1d),

    /**
     * Notation: (C0)
     * Representation: 01/12=0x1c=''
     * Specific name: FS - FILE SEPARATOR
     * IS4 is used to separate and qualify data logically; its specific meaning has to be defined for each
     * application. If this control function is used in hierarchical order, it may delimit a data item called a file,
     * see 8.2.10.
     */
    IS4_INFORMATION_SEPARATOR_FOUR(0x1c),

    /**
     * Notation: (C0)
     * Representation: 00/10=0xa=''
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to PRESENTATION, LF causes the
     * active presentation position to be moved to the corresponding character position of the following line in
     * the presentation component.
     * If the DEVICE COMPONENT SELECT MODE (DCSM) is set to DATA, LF causes the active data
     * position to be moved to the corresponding character position of the following line in the data
     * component.
     */
    LF_LINE_FEED(0x0a),

    /**
     * Notation: (C0)
     * Representation: 00/15=0xf=''
     * LS0 is used for code extension purposes. It causes the meanings of the bit combinations following it in
     * the data stream to be changed.
     * The use of LS0 is defined in Standard ECMA-35.
     * NOTE
     * LS0 is used in 8-bit environments only; in 7-bit environments SHIFT-IN (SI) is used instead.
     */
    LS0_LOCKING_SHIFT_ZERO(0x0f),

    /**
     * Notation: (C0)
     * Representation: 00/14=0xe=''
     * LS1 is used for code extension purposes. It causes the meanings of the bit combinations following it in
     * the data stream to be changed.
     * The use of LS1 is defined in Standard ECMA-35.
     * NOTE
     * LS1 is used in 8-bit environments only; in 7-bit environments SHIFT-OUT (SO) is used instead.
     */
    LS1_LOCKING_SHIFT_ONE(0x0e),

    /**
     * Notation: (C0)
     * Representation: 01/05=0x15=''
     * NAK is transmitted by a receiver as a negative response to the sender.
     * The use of NAK is defined in ISO 1745.
     */
    NAK_NEGATIVE_ACKNOWLEDGE(0x15),

    /**
     * Notation: (C0)
     * Representation: 00/00=0x0=' '
     * NUL is used for media-fill or time-fill. NUL characters may be inserted into, or removed from, a data
     * stream without affecting the information content of that stream, but such action may affect the
     * information layout and/or the control of equipment.
     */
    NUL_NULL(0x00),

    /**
     * Notation: (C0)
     * Representation: 00/15=0xf=''
     * SI is used for code extension purposes. It causes the meanings of the bit combinations following it in the
     * data stream to be changed.
     * The use of SI is defined in Standard ECMA-35.
     * NOTE
     * SI is used in 7-bit environments only; in 8-bit environments LOCKING-SHIFT ZERO (LS0) is used
     * instead.
     */
    SI_SHIFT_IN(0x0f),

    /**
     * Notation: (C0)
     * Representation: 00/14=0xe=''
     * SO is used for code extension purposes. It causes the meanings of the bit combinations following it in
     * the data stream to be changed.
     * The use of SO is defined in Standard ECMA-35.
     * NOTE
     * SO is used in 7-bit environments only; in 8-bit environments LOCKING-SHIFT ONE (LS1) is used
     * instead.
     */
    SO_SHIFT_OUT(0x0e),

    /**
     * Notation: (C0)
     * Representation: 00/01=0x1=''
     * SOH is used to indicate the beginning of a heading.
     * The use of SOH is defined in ISO 1745.
     */
    SOH_START_OF_HEADING(0x01),

    /**
     * Notation: (C0)
     * Representation: 00/02=0x2=''
     * STX is used to indicate the beginning of a text and the end of a heading.
     * The use of STX is defined in ISO 1745.
     */
    STX_START_OF_TEXT(0x02),

    /**
     * Notation: (C0)
     * Representation: 01/10=0x1a=''
     * SUB is used in the place of a character that has been found to be invalid or in error. SUB is intended to
     * be introduced by automatic means.
     */
    SUB_SUBSTITUTE(0x1a),

    /**
     * Notation: (C0)
     * Representation: 01/06=0x16=''
     * SYN is used by a synchronous transmission system in the absence of any other character (idle condition) to
     * provide a signal from which synchronism may be achieved or retained between data terminal equipment.
     * The use of SYN is defined in ISO 1745.
     */
    SYN_SYNCHRONOUS_IDLE(0x16),

    /**
     * Notation: (C0)
     * Representation: 00/11=0xb=''
     * VT causes the active presentation position to be moved in the presentation component to the
     * corresponding character position on the line at which the following line tabulation stop is set.
     */
    VT_LINE_TABULATION(0x0b);

    private final String pattern;

    private C0ControlFunction(int code) {
        char c = (char) code;
        this.pattern = String.valueOf(c);
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public ControlFunctionType getType() {
        return ControlFunctionType.C0_SET;
    }

    @Override
    public List<Object> getDefaultValues() {
        return null;
    }
}
