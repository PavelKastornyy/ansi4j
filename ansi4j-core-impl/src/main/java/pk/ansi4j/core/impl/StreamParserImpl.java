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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.ansi4j.core.api.FailureReason;
import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.FunctionFailureReason;
import pk.ansi4j.core.api.Parser;
import pk.ansi4j.core.api.ParserFactory;
import pk.ansi4j.core.api.StreamParser;

/**
 *
 * @author Pavel Kastornyy
 */
public class StreamParserImpl extends AbstractParser implements StreamParser {

    private static final Logger logger = LoggerFactory.getLogger(StreamParserImpl.class);

    private final InputStream stream;

    private final InputStreamReader inputStreamReader;

    private final BufferedReader bufferedReader;

    private final Charset encoding;

    private final int bufferSize;

    private final CharBuffer buffer;

    private final ParserFactory factory;

    /**
     * Surrogate that doesn't have pair.
     */
    private Character highSurrogate = null;

    public StreamParserImpl(InputStream stream, Charset encoding, int bufferSize, ParserFactory factory) {
        super("", factory);
        this.stream = stream;
        this.encoding = encoding;
        this.bufferSize = bufferSize;
        this.buffer = CharBuffer.allocate(bufferSize);
        this.factory = factory;
        inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        bufferedReader = new BufferedReader(inputStreamReader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fragment parse() {
        try {
            //there can text, saved function result etc
            var fragment = this.doParse();
            if (fragment != null) {
                return fragment;
            }
            while(true) {
                var count = this.readText();
                if (count == -1) {
                    return null;
                } else {
                    fragment = this.doParse();
                    if (fragment != null) {
                        return fragment;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error parsing text", ex);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (this.bufferedReader != null) {
            this.bufferedReader.close();
        }
        if (this.inputStreamReader != null) {
            this.inputStreamReader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean delayFunctionParsing(FailureReason reason) {
        if (reason == FunctionFailureReason.NO_END_OF_FUNCTION) {
            return true;
        } else {
            return false;
        }
    }

    private int readText() {
        try {
            int count = bufferedReader.read(buffer);
            if (count != -1) {
                buffer.flip();
                //we can have high surrogate without low one
                var readString = buffer.toString();
                buffer.clear();
                if (highSurrogate != null) {
                    readString = highSurrogate + readString;
                    highSurrogate = null;
                }
                var lastChar = readString.charAt(readString.length() - 1);
                if (Character.isHighSurrogate(lastChar)) {
                    readString = readString.substring(0, readString.length() - 1);
                    highSurrogate = lastChar;
                }
                this.setText(getText() + readString);
            }
            return count;
        } catch (IOException ex) {
            logger.error("Error reading stream", ex);
            return -1;
        }
    }

}
