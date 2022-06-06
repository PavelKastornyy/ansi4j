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
import java.util.Optional;
import pk.ansi4j.core.api.Configuration;
import pk.ansi4j.core.api.Environment;
import pk.ansi4j.core.api.function.FunctionArgument;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.C1ControlFunction;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.function.impl.FunctionArgumentImpl;
import pk.ansi4j.core.impl.FunctionFragmentImpl;
import pk.ansi4j.core.api.FunctionFailureReason;
import pk.ansi4j.core.api.FunctionParserResult;
import pk.ansi4j.core.api.iso6429.ControlFunction;
import pk.ansi4j.core.impl.FunctionParserResultImpl;


/**
 *
 * @author Pavel Kastornyy
 */
public class ControlStringParser extends AbstractFunctionParser {

    private Configuration config;

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getTargetFunctionType() {
        return ControlFunctionType.CONTROL_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionParserResult parse(String text, ControlFunction function, int currentIndex) {
        var startIndex = 0;
        String openingDelimiter = null;
        String terminatingTerminator = null;
        if (this.config.getEnvironment() == Environment._7_BIT) {
            openingDelimiter = ((C1ControlFunction) function).getPattern();
            terminatingTerminator = C1ControlFunction.ST_STRING_TERMINATOR.getPattern();
        } else if (this.config.getEnvironment() == Environment._8_BIT) {
            openingDelimiter = ((C1ControlFunction) function).get8BitPattern();
            terminatingTerminator = C1ControlFunction.ST_STRING_TERMINATOR.get8BitPattern();
        }
        int endIndex = text.indexOf(terminatingTerminator, startIndex);
        if (endIndex == -1) {
            return new FunctionParserResultImpl(Optional.empty(), FunctionFailureReason.NO_END_OF_FUNCTION);
        }
        endIndex += terminatingTerminator.length();
        var functionText = text.substring(startIndex, endIndex);
        var argumentString = text.substring(startIndex + openingDelimiter.length(), endIndex);
        var arguments = new ArrayList<FunctionArgument>();
        if (argumentString.indexOf(";") != -1) {
            var splits = argumentString.split(";");
            for (var split : splits) {
                var argument = new FunctionArgumentImpl(split, false);
                arguments.add(argument);
            }
        } else {
            var argument = new FunctionArgumentImpl(argumentString, false);
            arguments.add(argument);
        }
        return new FunctionParserResultImpl(Optional.of(
                new FunctionFragmentImpl(functionText, currentIndex, function, arguments)), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Configuration config) {
        this.config = config;

    }
}
