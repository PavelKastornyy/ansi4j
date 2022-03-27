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
package pk.ansi4j.css.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pk.ansi4j.css.api.Attribute;
import pk.ansi4j.css.api.AttributeChange;
import pk.ansi4j.css.api.AttributeContext;
import pk.ansi4j.css.api.AttributeCssGenerator;
import pk.ansi4j.css.api.text.TextAttribute;

/**
 *
 * @author Pavel Kastornyy
 */
public abstract class AbstractCssGenerator implements AttributeCssGenerator {


    @FunctionalInterface
    protected static interface DeclarationGenerator {

        /**
         * Generates CSS declarations and add them to declaration list.
         *
         * @param change
         * @param declarations
         */
        List<String> generate(AttributeChange change, AttributeContext context);
    }

    private Map<Attribute, DeclarationGenerator> generatorsByAttribuite = new HashMap<>();

    protected Map<Attribute, DeclarationGenerator> getGeneratorsByAttribuiteKey() {
        return generatorsByAttribuite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generate(AttributeChange change, AttributeContext context) {
        var generator = generatorsByAttribuite.get(change.getAttribute());
        var declarations =  generator.generate(change, context);
        return declarations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Attribute> getTargetAttributeClass() {
        return TextAttribute.class;
    }

    /**
     * Returns foreground color in accordance with REVERSE_VIDEO
     * @param context
     * @return
     */
    protected int getResolvedForegroundColor(AttributeContext context) {
        if (context.getNonDefaultValuesByAttribute().containsKey(TextAttribute.REVERSE_VIDEO)) {
            var bgColor = (int) context.getAttribute(TextAttribute.BACKGROUND_COLOR).getValue();
            return bgColor;
        } else {
            var fgColor = (int) context.getAttribute(TextAttribute.FOREGROUND_COLOR).getValue();
            return fgColor;
        }
    }

    /**
     * Returns background color in accordance with REVERSE_VIDEO
     * @param context
     * @return
     */
    protected int getResolvedBackgroundColor(AttributeContext context) {
        if (context.getNonDefaultValuesByAttribute().containsKey(TextAttribute.REVERSE_VIDEO)) {
            var fgColor = (int) context.getAttribute(TextAttribute.FOREGROUND_COLOR).getValue();
            return fgColor;
        } else {
            var bgColor = (int) context.getAttribute(TextAttribute.BACKGROUND_COLOR).getValue();
            return bgColor;
        }
    }

    protected String toHexColor(int i) {
        return String.format("%06X", i);
    }
}
