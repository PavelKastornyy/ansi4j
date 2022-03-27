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

import java.util.Objects;
import java.util.OptionalInt;
import pk.ansi4j.css.api.AttributeValue;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultAttributeValue implements AttributeValue {

    private final OptionalInt index;

    private final Object value;

    private final Object flag;

    public DefaultAttributeValue(Integer index, Object value) {
        this(index, value, null);
    }

    public DefaultAttributeValue(Integer index, Object value, Object flag) {
        if (index == null) {
            this.index = OptionalInt.empty();
        } else {
            this.index = OptionalInt.of(index);
        }
        this.value = value;
        this.flag = flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalInt getIndex() {
        return this.index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getFlag() {
        return this.flag;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.index);
        hash = 11 * hash + Objects.hashCode(this.value);
        hash = 11 * hash + Objects.hashCode(this.flag);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultAttributeValue other = (DefaultAttributeValue) obj;
        if (!Objects.equals(this.index, other.index)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return Objects.equals(this.flag, other.flag);
    }
}
