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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.ansi4j.core.api.Configuration;
import pk.ansi4j.core.api.Environment;
import pk.ansi4j.core.api.FragmentParserResult;
import static pk.ansi4j.core.api.FragmentParserResult.FailureReason;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.FunctionParser;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.impl.FunctionFragmentImpl;
import pk.ansi4j.core.api.FunctionFinderResult;
import pk.ansi4j.core.api.iso6429.ControlFunction;
import pk.ansi4j.core.impl.FragmentParserResultImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class C1ControlFunctionParser extends AbstractFunctionParser {

    private Configuration config;

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getTargetFunctionType() {
        return ControlFunctionType.C1_SET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentParserResult<FunctionFragment> parse(String text, ControlFunction function, int currentIndex) {
        var startIndex = 0;
        int endIndex;
        if (this.config.getEnvironment() == Environment._7_BIT) {
            endIndex = startIndex + 2;
        } else {
            endIndex = startIndex + 1;
        }
        if (!isEndOfFunctionPresent(text, endIndex)) {
            return new FragmentParserResultImpl<>(Optional.empty(), FailureReason.NO_END_OF_FUNCTION);
        }
        var functionText = text.substring(startIndex, endIndex);
        return new FragmentParserResultImpl<>(Optional.of(
                new FunctionFragmentImpl(functionText, currentIndex, function, new ArrayList<>())), null);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Configuration config) {
        this.config = config;

    }
}
