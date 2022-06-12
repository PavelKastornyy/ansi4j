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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pk.ansi4j.core.api.Environment;
import pk.ansi4j.core.api.FunctionFinder;
import pk.ansi4j.core.api.ParserFactory;
import pk.ansi4j.core.api.StreamParser;
import pk.ansi4j.core.api.StringParser;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.impl.StreamParserImpl;
import pk.ansi4j.core.impl.StringParserImpl;
import pk.ansi4j.core.api.FunctionHandler;
import pk.ansi4j.core.api.TextHandler;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultParserFactory implements ParserFactory {

    public static class Builder {

        private Environment environment;

        private FunctionFinder functionFinder;

        private Map<FunctionType, FunctionHandler> functionHandlersByType = new HashMap<>();

        private TextHandler textHandler;

        public Builder() {
            //empty constructor
        }

        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public Builder functionFinder(FunctionFinder finder) {
            this.functionFinder = finder;
            return this;
        }

        public Builder functionHandlers(FunctionHandler ... handlers) {
            Arrays.asList(handlers).forEach(p -> this.functionHandlersByType.put(p.getTargetFunctionType(), p));
            return this;
        }

        public Builder textHandler(TextHandler parser) {
            this.textHandler = parser;
            return this;
        }

        public ParserFactory build() {
            this.validate();
            var factory = new DefaultParserFactory(this);
            return factory;
        }

        private void validate() {
            if (environment == null) {
                throw new IllegalStateException("No environment");
            }
            if (functionFinder == null) {
                throw new IllegalStateException("No function finder");
            }
            if (this.functionHandlersByType.isEmpty()) {
                throw new IllegalStateException("No function handlers");
            }
            if (this.textHandler == null) {
                throw new IllegalStateException("No text handler");
            }
        }
    }

    private final Environment environment;

    private final FunctionFinder functionFinder;

    private final Map<FunctionType, FunctionHandler> functionHandlersByType;

    private final TextHandler textHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionFinder getFunctionFinder() {
        return this.functionFinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<FunctionType, FunctionHandler> getFunctionHandlersByType() {
        return Collections.unmodifiableMap(this.functionHandlersByType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextHandler getTextHandler() {
        return this.textHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringParser createParser(String text) {
        if (text == null) {
            throw new IllegalArgumentException("No text provided");
        }
        return new StringParserImpl(text, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamParser createParser(InputStream stream, Charset encoding, int bufferSize) {
        if (stream == null) {
            throw new IllegalArgumentException("No stream provided");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("No encoding provided");
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal value of buffer size: " + bufferSize);
        }
        return new StreamParserImpl(stream, encoding, bufferSize, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    private DefaultParserFactory(Builder builder) {
        this.environment = builder.environment;
        this.functionFinder = builder.functionFinder;
        this.functionFinder.initialize(this.environment);
        this.functionHandlersByType = new HashMap(builder.functionHandlersByType);
        this.functionHandlersByType.values().forEach(p -> p.initialize(this.environment));
        this.textHandler = builder.textHandler;
        this.textHandler.initialize(this.environment);
    }
}
