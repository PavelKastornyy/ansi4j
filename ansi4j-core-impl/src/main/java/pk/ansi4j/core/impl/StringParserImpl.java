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

import java.util.Arrays;
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
public class StringParserImpl implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(StringParserImpl.class);

    private String text;

    private final ParserFactory factory;

    private FunctionFragment savedFragment;

    private int currentIndex = 0;

    public StringParserImpl(String text, ParserFactory factory) {
        this.text = text;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasMoreFragments() {
        return text.length() > 0;
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
    public Fragment parseNextFragment() {
        if (savedFragment != null) {
            this.updateTextData(savedFragment.getText().length());
            var functionFragment = savedFragment;
            savedFragment = null;
            return functionFragment;
        }
        var functionIndex = this.parseNextFunction();
        if (functionIndex == -1) {
            //there are no functions
            var t = factory.getTextParser().parse(text, currentIndex).getFragment().get();
            this.updateTextData(text.length());
            return t;
        } else {
            //there is a function
            if (functionIndex == 0) {
                //there is no text before function
                this.updateTextData(savedFragment.getText().length());
                var funciontFragment = savedFragment;
                savedFragment = null;
                return funciontFragment;
            } else {
                //there is a text before function
                var piece = text.substring(0, functionIndex);
                var textFragment = factory.getTextParser().parse(piece, currentIndex).getFragment().get();
                this.updateTextData(piece.length());
                return textFragment;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Finds, parses next function, saves it and returns function index.
     * @return index or -1 if function wasn't found.
     */
    private int parseNextFunction() {
        var finder = factory.getFunctionFinder();
        var parsersByType = factory.getFunctionParsersByType();
        int internalIndex = - 1;
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
                    var piece = text.substring(internalIndex);
                    var resultFragment = parser.parse(piece, fr.getFunction(), internalIndex + currentIndex).getFragment();
                    if (resultFragment.isEmpty()) {
                        continue;
                    }
                    savedFragment = resultFragment.get();
                    return internalIndex;
                } catch (Exception ex) {
                    //debug level as we don't get errors for cases when function not supported, has mistakes in text etc
                    logger.debug("Error parsing function", ex);
                    continue;
                }

            }
        }
    }

    private void updateTextData(int length) {
        text = text.substring(length);
        currentIndex += length;
    }

}
