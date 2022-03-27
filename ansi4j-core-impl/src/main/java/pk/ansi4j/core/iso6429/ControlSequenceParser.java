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
import java.util.List;
import java.util.Optional;
import pk.ansi4j.core.api.Configuration;
import pk.ansi4j.core.api.FinderResult;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.impl.FunctionMatcher;
import pk.ansi4j.core.api.FunctionParser;
import pk.ansi4j.core.api.function.FunctionArgument;
import pk.ansi4j.core.impl.FunctionDescriptor;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.function.impl.FunctionArgumentImpl;
import pk.ansi4j.core.impl.FunctionFragmentImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class ControlSequenceParser extends AbstractControlSequenceTool implements FunctionParser {

    private Configuration config;

    private final FunctionMatcher matcher = new ControlSequenceMatcher();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FunctionFragment> parse(String text, FinderResult result) {
        var startIndex = result.getFunctionPosition();
        FunctionDescriptor functionDescriptor = this.matcher.match(startIndex, text);
        if (functionDescriptor == null) {
            return Optional.empty();
        }
        //getting text that will be parsed
        var codes = functionDescriptor.getCodes();
        final var finalByteIndex = text.indexOf(codes.get(codes.size() - 1), startIndex);
        var functionText = text.substring(startIndex, finalByteIndex + 1);
        String argStr = null;
        //getting arguments
        if (this.isIntermediateByte(functionText.codePointAt(functionText.length() - 1))) {
            argStr = functionText.substring(2, functionText.length() - 2);
        } else {
            argStr = functionText.substring(2, functionText.length() - 1);
        }
        List<FunctionArgument> arguments = this.parseArguments(argStr, functionDescriptor);
        var fragment = new FunctionFragmentImpl(startIndex, finalByteIndex + 1, functionText,
                functionDescriptor.getFunction(), arguments);
        return Optional.of(fragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getTargetFunctionType() {
        return ControlFunctionType.CONTROL_SEQUENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Configuration config) {
        this.config = config;
    }

    List<FunctionArgument> parseArguments(String argStr, FunctionDescriptor functionDescriptor) {
        //no arguments
        if (functionDescriptor.getParameters() == null) {
            return null;
        }
        List<FunctionArgument> arguments = new ArrayList<>();
        List<String> strArgs = this.parseArguments(argStr);
        if (strArgs == null) {
            var function = functionDescriptor.getFunction();
            if (function.getDefaultValues() != null) {
                var value = function.getDefaultValues().get(0);
                arguments.add(new FunctionArgumentImpl(value, true));
            }
        } else {
            var defaultValues = functionDescriptor.getFunction().getDefaultValues();
            for (var i = 0; i < strArgs.size(); i++) {
                var strArg = strArgs.get(i);
                FunctionArgument arg = null;
                if (strArg == null) {
                    //it is default value
                    if (defaultValues == null || defaultValues.size() - 1 < i) {
                        throw new IllegalArgumentException("Not default value with index " + i + " for "
                                + functionDescriptor.getFunction() + " arguments [" + argStr + "]");
                    } else {
                        arg = new FunctionArgumentImpl(defaultValues.get(i), true);
                    }
                } else {
                    if (this.isNumber(strArg)) {
                        arg = new FunctionArgumentImpl(Integer.valueOf(strArg), false);
                    } else {
                        arg = new FunctionArgumentImpl(strArg, false);
                    }
                }
                arguments.add(arg);
            }
        }
        return arguments;

    }

}
