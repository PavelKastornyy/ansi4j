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
package pk.ansi4j.core.api;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;
import pk.ansi4j.core.api.function.FunctionType;

/**
 *
 * @author Pavel Kastornyy
 */
@ThreadSafe
public interface ParserFactory {

    /**
     * Returns configuration.
     * @return
     */
    Configuration getConfiguration();

    /**
     * Returns thread-safe type of finder.
     *
     * @return
     */
    FunctionFinder getFunctionFinder();

    /**
     * Returns thread-safe function parsers by type.
     *
     * @return
     */
    Map<FunctionType, FunctionParser> getFunctionParsersByType();

    /**
     * Returns thread-safe text parser.
     *
     * @return
     */
    TextParser getTextParser();

    /**
     * Creates NOT thread-safe string parser that will use thread-safe components.
     *
     * @param text
     * @return
     */
    StringParser createParser(String text);

    /**
     * Creates NOT thread-safe stream parser that will use thread-safe components.
     *
     * @param stream
     * @param encoding
     * @return
     */
    StreamParser createParser(InputStream stream, Charset encoding, int bufferSize);
}
