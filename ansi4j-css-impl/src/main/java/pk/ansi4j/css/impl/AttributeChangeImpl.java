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
package pk.ansi4j.css.impl;

import pk.ansi4j.css.api.Attribute;
import pk.ansi4j.css.api.AttributeChange;
import pk.ansi4j.css.api.AttributeValue;

/**
 *
 * @author Pavel Kastornyy
 */
public class AttributeChangeImpl implements AttributeChange {

    private final Attribute key;

    private final AttributeValue oldValue;

    private final AttributeValue newValue;

    public AttributeChangeImpl(Attribute attribute, AttributeValue oldValue, AttributeValue newValue) {
        this.key = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute getAttribute() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getOldValue() {
        return this.oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getNewValue() {
        return this.newValue;
    }


}
