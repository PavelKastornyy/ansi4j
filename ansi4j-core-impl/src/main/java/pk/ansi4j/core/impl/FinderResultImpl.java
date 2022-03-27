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

import pk.ansi4j.core.api.FinderResult;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunction;

/**
 *
 * @author Pavel Kastornyy
 */
public class FinderResultImpl implements FinderResult {

    private final int functionPosition;

    private final FunctionType functionType;

    private final ControlFunction firstFunction;

    public FinderResultImpl(int functionPosition, FunctionType functionType, ControlFunction firstFunction) {
        this.functionPosition = functionPosition;
        this.functionType = functionType;
        this.firstFunction = firstFunction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFunctionPosition() {
        return this.functionPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionType getFunctionType() {
        return this.functionType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControlFunction getFirstFunction() {
        return firstFunction;
    }
}
