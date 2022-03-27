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
import pk.ansi4j.core.api.FinderResult;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.FunctionParser;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunctionType;
import pk.ansi4j.core.impl.FunctionFragmentImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class C0ControlFunctionParser implements FunctionParser {

    private Configuration config;

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getTargetFunctionType() {
        return ControlFunctionType.C0_SET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FunctionFragment> parse(String text, FinderResult result) {
        if (result.getFirstFunction() == null) {
            return Optional.empty();
        } else {
            var startIndex = result.getFunctionPosition();
            int endIndex = startIndex + 1;
            var functionText = text.substring(startIndex, endIndex);
            var function = result.getFirstFunction();
            return Optional.of(
                    new FunctionFragmentImpl(startIndex, endIndex, functionText, function, new ArrayList<>()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Configuration config) {
        this.config = config;

    }

}
