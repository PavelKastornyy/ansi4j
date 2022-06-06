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

import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.FragmentType;

/**
 *
 * @author Pavel Kastornyy
 */
abstract class AbstractFragment implements Fragment {

    private final FragmentType type;

    private final String text;

    private final int startIndex;

    private final int endIndex;

    public AbstractFragment(FragmentType type, String text, int currentIndex) {
        this.type = type;
        this.text = text;
        this.startIndex = currentIndex;
        this.endIndex = currentIndex + text.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AbstractFragmentImpl{" + "type=" + type + ", text=" + text + ", startIndex=" + startIndex
                + ", endIndex=" + endIndex + '}';
    }
}
