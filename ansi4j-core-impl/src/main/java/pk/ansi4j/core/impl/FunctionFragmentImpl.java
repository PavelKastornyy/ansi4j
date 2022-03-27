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
package pk.ansi4j.core.impl;

import java.util.Collections;
import java.util.List;
import pk.ansi4j.core.api.FragmentType;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.function.Function;
import pk.ansi4j.core.api.function.FunctionArgument;

/**
 *
 * @author Pavel Kastornyy
 */
public class FunctionFragmentImpl extends AbstractFragmentImpl implements FunctionFragment {

    private final Function function;

    private final List<FunctionArgument> arguments;

    /**
     *
     * @param text
     * @param function
     * @param arguments modifiable collection.
     */
    public FunctionFragmentImpl(int startIndex, int endIndex, String text,
            Function function, List<FunctionArgument> arguments) {
        super(FragmentType.FUNCTION, startIndex, endIndex, text);
        this.function = function;
        if (arguments != null) {
            this.arguments = Collections.unmodifiableList(arguments);
        } else {
            this.arguments = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function getFunction() {
        return this.function;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FunctionArgument> getArguments() {
        return this.arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FunctionFragmentImpl{" + "function=" + function + ", arguments=" + arguments + '}'
                + "->" + super.toString();
    }
}
