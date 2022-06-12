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

import java.util.Optional;
import pk.ansi4j.core.api.FunctionFailureReason;
import pk.ansi4j.core.api.TextFragment;
import pk.ansi4j.core.api.TextHandlerResult;

/**
 *
 * @author Pavel Kastornyy
 */
public class TextHandlerResultImpl extends AbstractFragmentHandlerResult implements TextHandlerResult {

    private final Optional<TextFragment> fragment;

    public TextHandlerResultImpl(Optional<TextFragment> fragment, FunctionFailureReason failureReason) {
        super(failureReason);
        this.fragment = fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<TextFragment> getFragment() {
        return this.fragment;
    }

}
