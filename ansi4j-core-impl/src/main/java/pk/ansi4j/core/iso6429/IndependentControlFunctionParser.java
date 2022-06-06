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
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.impl.FunctionFragmentImpl;
import pk.ansi4j.core.api.FunctionFailureReason;
import pk.ansi4j.core.api.FunctionParserResult;
import pk.ansi4j.core.api.iso6429.ControlFunction;
import pk.ansi4j.core.impl.FunctionParserResultImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class IndependentControlFunctionParser extends AbstractFunctionParser {

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getTargetFunctionType() {
        return ControlFunctionType.INDEPENDENT_FUNCTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionParserResult parse(String text, ControlFunction function, int currentIndex) {
        var startIndex = 0;
        int endIndex = startIndex + 2;
        if (!isEndOfFunctionPresent(text, endIndex)) {
            return new FunctionParserResultImpl(Optional.empty(), FunctionFailureReason.NO_END_OF_FUNCTION);
        }
        var functionText = text.substring(startIndex, endIndex);
        return new FunctionParserResultImpl(Optional.of(
                new FunctionFragmentImpl(functionText, currentIndex, function, new ArrayList<>())), null);
    }
}
