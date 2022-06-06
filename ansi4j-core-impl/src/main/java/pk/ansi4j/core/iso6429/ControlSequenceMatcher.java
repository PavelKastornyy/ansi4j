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
package pk.ansi4j.core.iso6429;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.ansi4j.core.impl.FunctionMatcher;
import pk.ansi4j.core.impl.FunctionDescriptor;
import pk.ansi4j.core.api.iso6429.ControlFunction;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction;
import pk.ansi4j.core.api.utils.Characters;
import pk.ansi4j.core.impl.FunctionDescriptor;

/**
 *
 * @author Pavel Kastornyy
 */
class ControlSequenceMatcher implements FunctionMatcher {

    private static final Logger logger = LoggerFactory.getLogger(ControlSequenceMatcher.class);

    /**
     * Identifier is one or two letter (first space) string.
     */
    private Map<String, FunctionDescriptor> descriptorsByIdentifier = new HashMap<>();

    public ControlSequenceMatcher() {
        this(Arrays.asList(ControlSequenceFunction.values()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionDescriptor match(int startIndex, String functionText) {
        if (functionText.charAt(startIndex) != Characters.ESC ||
                functionText.charAt(startIndex + 1) != Characters.LEFT_SB) {
            return null;
        }
        String intermediateByte = "";
        String finalByte = null;
        for (int offset = startIndex + 2; offset < functionText.length();) {
            final int codepoint = functionText.codePointAt(offset);
            if (ControlSequenceUtils.isIntermediateByte(codepoint)) {
                intermediateByte = new String(Character.toChars(codepoint));
            } else if (ControlSequenceUtils.isFinalByte(codepoint)) {
                finalByte = new String(Character.toChars(codepoint));
                break;
            }
            offset += Character.charCount(codepoint);
        }
        if (finalByte != null) {
            return this.descriptorsByIdentifier.get(intermediateByte + finalByte);
        } else {
            return null;
        }
    }

    /**
     * Constructor.
     * @param <T>
     * @param functions classes of ControlSequence enumerations.
     */
    ControlSequenceMatcher(List<ControlFunction> functions) {
        if (functions == null || functions.isEmpty()) {
            throw new IllegalArgumentException("No function enumeration classes");
        }
        for (var function : functions) {
            if (function.getType() == ControlFunctionType.CONTROL_SEQUENCE) {
                this.addControlSequence(function);
            }
        }
        logger.debug("Added {} control sequence functions to index", this.descriptorsByIdentifier.size());
    }

    /**
     * Returns forward codes of the pattern including ESC and [. Parameter placeholders are returned as `{?}`.
     *
     * @param pattern that does NOT include ESC and [.
     *
     * @return
     */
    FunctionDescriptor createDescriptor(ControlFunction function) {
        List<String> codes = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        var pattern  = function.getPattern();
        //now we split it by;
        var splits = pattern.split(Pattern.quote(";"));
        var digitStringBuilder = new StringBuilder();
        String notDigit = null;
        for (var splitIndex = 0; splitIndex < splits.length; splitIndex++) {
            var split = splits[splitIndex];
            for(int charIndex = 0; charIndex < split.length();){
                final char c = split.charAt(charIndex);
                //if it is a digit
                if(c > 47 && c < 58){
                    digitStringBuilder.append(c);
                    charIndex++;
                //if it is a `{`
                } else if (c == 123) {
                    if (charIndex + 2 < split.length() && split.charAt(charIndex + 2) == 125) {
                        notDigit = "{" + split.charAt(charIndex + 1) + "}";
                        parameters.add(notDigit);
                        charIndex = charIndex + 3;
                    } else {
                        throw new IllegalArgumentException("Found `{` without `}`");
                    }
                //all others
                } else {
                    notDigit = String.valueOf(c);
                    charIndex++;
                }
                //not digit or last cycle
                if (notDigit != null || charIndex == split.length()) {
                    if (digitStringBuilder.length() > 0) {
                        codes.add(digitStringBuilder.toString());
                        digitStringBuilder.setLength(0);
                    }
                    if (notDigit != null) {
                        codes.add(notDigit);
                        notDigit = null;
                    }
                }
            }
            //now we add removed `;`
            if (splitIndex + 1 != splits.length) {
                codes.add(";");
            }
        }
        if (parameters.isEmpty()) {
            parameters = null;
        }
        return new FunctionDescriptor(function, parameters, codes);
    }

    private void addControlSequence(ControlFunction function) {
        var descriptor = this.createDescriptor(function);
        var codes = descriptor.getCodes();
        var lastCode = codes.get(codes.size() - 1);
        var beforeLastCode = codes.get(codes.size() - 2);
        String identifier = null;
        if (beforeLastCode.equals(" ")) {
            identifier = beforeLastCode + lastCode;
        } else {
            identifier = lastCode;
        }
        if (!descriptorsByIdentifier.containsKey(identifier)) {
            descriptorsByIdentifier.put(identifier, descriptor);
        } else {
            throw new IllegalStateException("Identifier " + identifier + " already exists. Can't add " + function);
        }
    }
}
