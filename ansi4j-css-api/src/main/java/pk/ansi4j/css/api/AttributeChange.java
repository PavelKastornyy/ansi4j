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

import javax.annotation.concurrent.Immutable;

/**
 * Change in value of some attribute.
 *
 * @author Pavel Kastornyy
 */
@Immutable
public interface AttributeChange {

    /**
     * Returns attribute.
     *
     * @return
     */
    public Attribute getAttribute();

    /**
     * Returns old value of the attribute. It can be both default and non default values.
     *
     * @return
     */
    public AttributeValue getOldValue();

    /**
     * Returns new value of the attribute. It can be both default and non default values.
     * @return
     */
    public AttributeValue getNewValue();
}
