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
import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.FragmentParserResult;

/**
 *
 * @author Pavel Kastornyy
 */
public class FragmentParserResultImpl<T extends Fragment> implements FragmentParserResult<T> {

    private final Optional<T> fragment;

    private final FailureReason failureReason;

    public FragmentParserResultImpl(Optional<T> fragment, FailureReason failureReason) {
        this.fragment = fragment;
        this.failureReason = failureReason;
    }

    @Override
    public Optional<T> getFragment() {
        return this.fragment;
    }

    @Override
    public FailureReason getFailureReason() {
        return this.failureReason;
    }

}
