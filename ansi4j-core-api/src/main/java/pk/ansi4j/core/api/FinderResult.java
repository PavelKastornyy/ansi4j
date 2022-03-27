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
package pk.ansi4j.core.api;

import javax.annotation.concurrent.Immutable;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.api.iso6429.ControlFunction;

/**
 *
 * @author Pavel Kastornyy
 */
@Immutable
public interface FinderResult {

    /**
     * Returns position of function in text.
     *
     * @return
     */
    int getFunctionPosition();

    /**
     * Returns found function type. We don't take type from firstFunction because first function can hold C1 function
     * but type can be ControlString.
     *
     * @return
     */
    FunctionType getFunctionType();

    /**
     * For C0 returns C0 function, for C1 return C1 function, for sequence returns CSI, for independent returns
     * independent function, for control strings returns opening delimiter.
     *
     * @return
     */
    ControlFunction getFirstFunction();
}
