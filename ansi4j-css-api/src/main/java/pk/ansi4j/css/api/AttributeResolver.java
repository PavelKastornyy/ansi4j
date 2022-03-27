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
package pk.ansi4j.css.api;

import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.function.Function;

/**
 *
 * @author Pavel Kastornyy
 */
@ThreadSafe
public interface AttributeResolver {

    /**
     * Resolves attributes on the base of function and modifies context.
     *
     * @param functionFragment
     * @param context
     * @return changes or empty list.
     */
    List<AttributeChange> resolve(FunctionFragment functionFragment, AttributeContext context);

    /**
     * Returns function that this attribute resolver supports.
     * 
     * @return
     */
    Function getTargetFunction();
}
