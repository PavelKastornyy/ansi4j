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
package pk.ansi4j.css;

import java.util.List;
import pk.ansi4j.core.api.FragmentType;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.function.Function;
import pk.ansi4j.core.api.function.FunctionArgument;
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction;

/**
 *
 * @author Pavel Kastornyy
 */
public class FunctionFragmentImpl implements FunctionFragment {

    private List<FunctionArgument> arguments;

    public FunctionFragmentImpl(List<FunctionArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public Function getFunction() {
        return ControlSequenceFunction.SGR_SELECT_GRAPHIC_RENDITION;
    }

    @Override
    public List<FunctionArgument> getArguments() {
        return this.arguments;
    }

    @Override
    public FragmentType getType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getStartIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEndIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
