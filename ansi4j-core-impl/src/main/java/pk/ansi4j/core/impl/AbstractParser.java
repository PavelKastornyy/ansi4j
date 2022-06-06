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
import pk.ansi4j.core.api.FailureReason;
import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.FunctionFinderResult;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.FunctionParser;
import pk.ansi4j.core.api.FunctionParserResult;
import pk.ansi4j.core.api.Parser;
import pk.ansi4j.core.api.ParserFactory;

/**
 *
 * @author Pavel Kastornyy
 */
abstract class AbstractParser implements Parser {

    protected static enum FunctionProcessingResult {

        NOT_FOUND, FOUND_BUT_DELAYED, FOUND_AND_PARSED
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractParser.class);

    /**
     * Text can never be null.
     */
    private String text;

    private final ParserFactory factory;

    private FunctionFinderResult functionFinderResult;

    private FunctionParserResult functionParserResult;

    /**
     * As found function changes as text before function is parsed we use this variable.
     */
    private int foundFunctionIndex = -1;

    private int currentIndex = 0;

    public AbstractParser(String text, ParserFactory factory) {
        this.text = text;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    protected abstract boolean delayFunctionParsing(FailureReason reason);

    /**
     * Parses text field. So, when this method called this field can't be null.
     *
     * There are two possible variants: a) index in at the beginning of the function b) there is a text before a
     * function. In the case a) function is parsed and returned. In case b) function is parsed and saved, then
     * text is returned. With the second call saved function is returned.
     *
     * @return
     */
    protected Fragment doParse() {
        if (functionParserResult != null) {
            var functionFragment = this.functionParserResult.getFragment().get();
            this.updateTextData(functionFragment.getText().length());
            this.functionFinderResult = null;
            this.functionParserResult = null;
            return functionFragment;
        }
        if (text.length() == 0) {
            return null;
        }
        var functionProcessingResult = this.findAndParseFunction();
        var factory = this.getFactory();
        var currentIndex = this.getCurrentIndex();
        if (functionProcessingResult == FunctionProcessingResult.NOT_FOUND) {
            //there are no functions
            var t = factory.getTextParser().parse(text, currentIndex).getFragment().get();
            this.updateTextData(text.length());
            return t;
        } else if (functionProcessingResult == FunctionProcessingResult.FOUND_BUT_DELAYED) {
            var functionIndex = foundFunctionIndex;
            if (functionIndex > 0) {
                //there is a text before function
                var piece = text.substring(0, functionIndex);
                var textFragment = factory.getTextParser().parse(piece, currentIndex).getFragment().get();
                this.updateTextData(piece.length());
                foundFunctionIndex = 0;
                return textFragment;
            } else {
                return null;
            }
        } else {
            //there is a function
            var functionIndex = foundFunctionIndex;
            if (functionIndex == 0) {
                //there is no text before function
                var functionFragment = this.functionParserResult.getFragment().get();
                this.updateTextData(functionFragment.getText().length());
                this.functionFinderResult = null;
                this.functionParserResult = null;
                return functionFragment;
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
     * Finds, parses next function, saves finder and parser results.
     * @return
     */
    protected FunctionProcessingResult findAndParseFunction() {
        var finder = factory.getFunctionFinder();
        var parsersByType = factory.getFunctionParsersByType();
        int internalIndex = - 1;
        while (true) {
            internalIndex = internalIndex + 1;
            FunctionFinderResult finderResult = null;
            //we can use save finder result not to find again
            if (functionFinderResult == null) {
                this.foundFunctionIndex = -1;
                var finderResultOptional = finder.find(internalIndex, text);
                if (finderResultOptional.isEmpty()) {
                    return FunctionProcessingResult.NOT_FOUND;
                } else {
                    finderResult = finderResultOptional.get();
                    foundFunctionIndex = finderResult.getFunctionIndex();
                }
            } else {
                finderResult = this.functionFinderResult;
            }
            internalIndex = foundFunctionIndex;
            var parser = parsersByType.get(finderResult.getFunctionType());
            if (parser == null) {
                logger.warn("No parser for functionType={}", finderResult.getFunctionType());
                this.functionFinderResult = null;
                continue;
            }
            var piece = text.substring(internalIndex);
            var parserResult = parser.parse(piece, finderResult.getFunction(), internalIndex + currentIndex);
            var resultFragment = parserResult.getFragment();
            if (resultFragment.isEmpty()) {
                if (this.delayFunctionParsing(parserResult.getFailureReason())) {
                    this.functionFinderResult = finderResult;
                    this.functionParserResult = null;
                    return FunctionProcessingResult.FOUND_BUT_DELAYED;
                } else {
                    this.logFunctionFailure(finderResult, parserResult);
                    continue;
                }
            }
            this.functionFinderResult = finderResult;
            this.functionParserResult = parserResult;
            return FunctionProcessingResult.FOUND_AND_PARSED;
        }
    }

    protected void updateTextData(int length) {
        text = text.substring(length);
        currentIndex += length;
    }

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    protected ParserFactory getFactory() {
        return factory;
    }

    protected void logFunctionFailure(FunctionFinderResult finderResult,
            FunctionParserResult parserResult) {
        logger.warn("Couldn't parse function={} at index={}. Reason is {}",
                finderResult.getFunction(), this.getCurrentIndex() + finderResult.getFunctionIndex(),
                parserResult.getFailureReason());
    }
}
