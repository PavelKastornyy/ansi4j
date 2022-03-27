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

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * AttributeContext holds and manages attributes for some text including default and non default values.
 * One context can work with N attribute classes.
 *
 * @author Pavel Kastornyy
 */
@NotThreadSafe
public interface AttributeContext {

    /**
     * Returns non default values of the context in unmodifiable map.
     *
     * @return
     */
    Map<Attribute, AttributeValue> getNonDefaultValuesByAttribute();

    /**
     * Sets attribute in context and returns change, if there has been any modifications in value.
     *
     * @param value if value is null, then value is equal to default value.
     * @return change or empty optional.
     */
    Optional<AttributeChange> setAttribute(Attribute attribute, AttributeValue value);

    /**
     * Returns both default and non default values.
     *
     * @param attribute
     * @return
     */
    AttributeValue getAttribute(Attribute attribute);

    /**
     * Attribute configuration that this context uses for resolving values.
     *
     * @return
     */
    AttributeConfig getAttributeConfig(Class<? extends Attribute> attributeClass);

}
