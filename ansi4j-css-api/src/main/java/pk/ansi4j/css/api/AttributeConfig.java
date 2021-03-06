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
 *
 * @author Pavel Kastornyy
 */
@Immutable
public interface AttributeConfig {

    /**
     * Returns attribute class that this configuration supports.
     * @return
     */
    Class<? extends Attribute> getTargetAttribute();

    /**
     * Returns default value for the attribute.
     *
     * @param attribute
     * @return
     */
    AttributeValue getDefaultValue(Attribute attribute);

}
