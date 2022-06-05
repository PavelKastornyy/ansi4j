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
import pk.ansi4j.core.api.Configuration;
import pk.ansi4j.core.api.FunctionFinder;
import pk.ansi4j.core.api.FunctionParser;
import pk.ansi4j.core.api.Parser;
import pk.ansi4j.core.api.ParserFactory;
import pk.ansi4j.core.api.TextParser;
import pk.ansi4j.core.api.function.FunctionType;
import pk.ansi4j.core.impl.StringParserImpl;

/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultParserFactory implements ParserFactory {

    public static class Builder {

        private Configuration configuration;

        private FunctionFinder functionFinder;

        private Map<FunctionType, FunctionParser> functionParsersByType = new HashMap<>();

        private TextParser textParser;

        public Builder() {
            //empty constructor
        }

        public Builder configuration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder functionFinder(FunctionFinder finder) {
            this.functionFinder = finder;
            return this;
        }

        public Builder functionParsers(FunctionParser ... parsers) {
            Arrays.asList(parsers).forEach(p -> this.functionParsersByType.put(p.getTargetFunctionType(), p));
            return this;
        }

        public Builder textParser(TextParser parser) {
            this.textParser = parser;
            return this;
        }

        public ParserFactory build() {
            this.validate();
            var factory = new DefaultParserFactory(this);
            return factory;
        }

        private void validate() {
            if (configuration == null) {
                throw new IllegalStateException("No configuration");
            }
            if (functionFinder == null) {
                throw new IllegalStateException("No function finder");
            }
            if (this.functionParsersByType.isEmpty()) {
                throw new IllegalStateException("No function parsers");
            }
            if (this.textParser == null) {
                throw new IllegalStateException("No text parser");
            }
        }
    }

    private final Configuration configuration;

    private final FunctionFinder functionFinder;

    private final Map<FunctionType, FunctionParser> functionParsersByType;

    private final TextParser textParser;

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
    public Map<FunctionType, FunctionParser> getFunctionParsersByType() {
        return Collections.unmodifiableMap(this.functionParsersByType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextParser getTextParser() {
        return this.textParser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parser createParser(String text) {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("No text provided");
        }
        return new StringParserImpl(text, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parser createParser(InputStream stream, Charset encoding) {
        //return new StreamParserImpl(stream, encoding, this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    private DefaultParserFactory(Builder builder) {
        this.configuration = builder.configuration;
        this.functionFinder = builder.functionFinder;
        this.functionFinder.initialize(configuration);
        this.functionParsersByType = new HashMap(builder.functionParsersByType);
        this.functionParsersByType.values().forEach(p -> p.initialize(configuration));
        this.textParser = builder.textParser;
        this.textParser.initialize(configuration);
    }
}
