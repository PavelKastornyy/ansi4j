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
package pk.ansi4j.core;

import pk.ansi4j.core.api.Configuration;
import pk.ansi4j.core.api.Environment;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultConfiguration implements Configuration {

    public static class Builder {

        private Environment environment;

        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public Configuration build() {
            this.validate();
            var config = new DefaultConfiguration(this);
            return config;
        }

        private void validate() {
            if (this.environment == null) {
                throw new IllegalStateException("No environment");
            }
        }
    }

    private final Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    private DefaultConfiguration(Builder builder) {
        this.environment = builder.environment;
    }


}
