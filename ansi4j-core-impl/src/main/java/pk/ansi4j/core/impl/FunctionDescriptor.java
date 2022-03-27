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
import pk.ansi4j.core.api.function.Function;

/**
 *
 * @author Pavel Kastornyy
 */
public class FunctionDescriptor {

    private final Function function;

    private final List<String> parameters;

    private final List<String> codes;

    /**
     *
     * @param function
     * @param parameters modifiable collection of parameters.
     * @param codes modifiable collection codes.
     */
    public FunctionDescriptor(Function function, List<String> parameters, List<String> codes) {
        this.function = function;
        if (parameters != null) {
            this.parameters = Collections.unmodifiableList(parameters);
        } else {
            this.parameters = null;
        }
        this.codes = Collections.unmodifiableList(codes);
    }

    public Function getFunction() {
        return this.function;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public List<String> getCodes() {
        return this.codes;
    }

}
