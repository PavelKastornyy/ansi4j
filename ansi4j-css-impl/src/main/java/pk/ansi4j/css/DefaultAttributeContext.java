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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import pk.ansi4j.css.api.Attribute;
import pk.ansi4j.css.api.AttributeChange;
import pk.ansi4j.css.api.AttributeConfig;
import pk.ansi4j.css.api.AttributeContext;
import pk.ansi4j.css.api.AttributeValue;
import pk.ansi4j.css.impl.AttributeChangeImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultAttributeContext implements AttributeContext {

    private final Map<Attribute, AttributeValue> nonDefaultValuesByAttribute = new HashMap<>();

    private final Map<Class<? extends Attribute>, AttributeConfig> attributeConfigByClass = new HashMap<>();

    public DefaultAttributeContext(List<AttributeConfig> configs) {
        if (configs == null || configs.size() == 0) {
            throw new IllegalArgumentException("At least one config must be provided");
        }
        for (var config : configs) {
            attributeConfigByClass.put(config.getTargetAttribute(), config);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Attribute, AttributeValue> getNonDefaultValuesByAttribute() {
        return this.nonDefaultValuesByAttribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AttributeChange> setAttribute(Attribute attribute, AttributeValue newValue) {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute can't be null");
        }
        var defaultValue = this.provideDefaultValue(attribute);
        var oldValue = this.nonDefaultValuesByAttribute.get(attribute);
        if (oldValue == null) {
            oldValue = defaultValue;
        }
        if (newValue == null) {
            newValue = defaultValue;
        }
        if (Objects.equals(newValue, oldValue)) {
            //then do nothing
            return Optional.empty();
        } else {
            if (Objects.equals(newValue, defaultValue)) {
                this.nonDefaultValuesByAttribute.remove(attribute);
            } else {
                this.nonDefaultValuesByAttribute.put(attribute, newValue);
            }
            return Optional.of(new AttributeChangeImpl(attribute, oldValue, newValue));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue getAttribute(Attribute attribute) {
        var value = this.nonDefaultValuesByAttribute.get(attribute);
        if (value == null) {
            value = this.provideDefaultValue(attribute);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeConfig getAttributeConfig(Class<? extends Attribute> attributeClass) {
        return this.attributeConfigByClass.get(attributeClass);
    }

    private AttributeValue provideDefaultValue(Attribute attribute) {
        var config = this.attributeConfigByClass.get(attribute.getClass());
        if (config == null) {
            throw new NullPointerException("No config for attribute=" + attribute);
        }
        var value = config.getDefaultValue(attribute);
        return value;
    }

}
