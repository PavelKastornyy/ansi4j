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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.Parser;
import pk.ansi4j.core.api.ParserFactory;

/**
 *
 * @author Pavel Kastornyy
 */
public class ParserImpl implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(ParserImpl.class);

    private final String text;

    private final ParserFactory factory;

    private int index = 0;

    private FunctionFragment savedFragment;

    public ParserImpl(String text, ParserFactory factory) {
        this.text = text;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasMoreFragments() {
        if (index < text.length()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * There are two possible variants: a) index in at the beginning of the function b) there is a text before a
     * function. In the case a) function is parsed and returned. In case b) function is parsed and saved, then
     * text is returned. With the second call saved function is returned.
     *
     * @return
     */
    @Override
    public Fragment nextFragment() {
        if (savedFragment != null) {
            index += savedFragment.getText().length();
            var functionFragment = savedFragment;
            savedFragment = null;
            return functionFragment;
        }
        var functionIndex = this.parseNextFunction();
        if (functionIndex == -1) {
            //there are no functions
            var savedIndex = index;
            //set nor more fragments
            index = text.length();
            return factory.getTextParser().parse(savedIndex, index, text).get();
        } else {
            //there is a function
            if (functionIndex == index) {
                //there is no text before function
                index = index + savedFragment.getText().length();
                var funciontFragment = savedFragment;
                savedFragment = null;
                return funciontFragment;
            } else {
                //there is a text before function
                var textFragment = factory.getTextParser().parse(index, functionIndex, text).get();
                index = functionIndex;
                return textFragment;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrentIndex() {
        return index;
    }

    /**
     * Finds, parses next function, saves it and returns function index.
     * @return index or -1 if function wasn't found.
     */
    private int parseNextFunction() {
        var finder = factory.getFunctionFinder();
        var parsersByType = factory.getFunctionParsersByType();
        int internalIndex = index - 1;
        while (true) {
            internalIndex = internalIndex + 1;
            var finderResult = finder.find(internalIndex, text);
            if (finderResult.isEmpty()) {
                return -1;
            } else {
                try {
                    var fr = finderResult.get();
                    internalIndex = fr.getFunctionPosition();
                    var parser = parsersByType.get(fr.getFunctionType());
                    if (parser == null) {
                        continue;
                    }
                    var fragment = parser.parse(text, fr);
                    if (fragment.isEmpty()) {
                        continue;
                    }
                    savedFragment = fragment.get();
                    return internalIndex;
                } catch (Exception ex) {
                    //debug level as we don't get errors for cases when function not supported, has mistakes in text etc
                    logger.debug("Error parsing function", ex);
                    continue;
                }

            }
        }
    }

}
