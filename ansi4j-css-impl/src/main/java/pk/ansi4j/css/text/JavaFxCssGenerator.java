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
import pk.ansi4j.css.api.text.TextAttribute;
import pk.ansi4j.css.api.text.TextAttributeValue;

/**
 *
 * @author Pavel Kastornyy
 */
public class JavaFxCssGenerator extends AbstractCssGenerator {

    private DeclarationGenerator weightGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        switch (newValue) {
            case TextAttributeValue.WEIGHT_BOLD:
                declarations.add("-fx-font-weight: bold");
            break;
            case TextAttributeValue.WEIGHT_FAINT:
                declarations.add("-fx-font-weight: 300");
            break;
            case TextAttributeValue.WEIGHT_NORMAL:
                declarations.add("-fx-font-weight: normal");
            break;
            default:
                throw new IllegalArgumentException("Unknown value=" + newValue);
        }
        return declarations;
    };

    private DeclarationGenerator italicGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        switch (newValue) {
            case TextAttributeValue.ITALIC_ON:
                declarations.add("-fx-font-style: italic");
            break;
            case TextAttributeValue.ITALIC_OFF:
                declarations.add("-fx-font-style: normal");
            break;
            default:
                throw new IllegalArgumentException("Unknown value=" + newValue);
        }
        return declarations;
    };

    /**
     * We use "border-bottom" property here, but not "text-decoration-line: underline" because
     * "text-decoration-style: solid; text-decoration-line: line-through;" we use for STRIKETHROUGH. If we
     * set "text-decoration-style: double" for double underline then STRIKETHROUGH will be also double.
     */
    private DeclarationGenerator underlineGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        var fgColor = this.toHexColor(this.getResolvedForegroundColor(context));
        switch (newValue) {
            case TextAttributeValue.UNDERLINE_SINGLE:
                //one border line
                declarations.add("-fx-border-width: 0 0 1 0");
                declarations.add("-fx-border-insets: 0 0 1 0");
                declarations.add("-fx-border-color: #" + fgColor);
                //declarations.add("border-block-width: 3px");
            break;
            case TextAttributeValue.UNDERLINE_DOUBLE:
                //three border lines
                declarations.add("-fx-border-color: #" + fgColor +", transparent, #" + fgColor);
                declarations.add("-fx-border-width: 0 0 1 0, 0 0 1 0, 0 0 1 0");
                declarations.add("-fx-border-insets: 0 0 1 0, 0 0 2 0, 0 0 3 0");
            break;
            case TextAttributeValue.UNDERLINE_OFF:
                if (((int) change.getOldValue().getValue()) == TextAttributeValue.UNDERLINE_SINGLE) {
                    declarations.add("-fx-border-width: 0 0 0 0");
                    declarations.add("-fx-border-insets: 0 0 0 0");
                } else {
                    declarations.add("-fx-border-width: 0 0 0 0, 0 0 0 0, 0 0 0 0");
                    declarations.add("-fx-border-insets: 0 0 0 0, 0 0 0 0, 0 0 0 0");
                }
            break;
            default:
                throw new IllegalArgumentException("Unknown value=" + newValue);
        }
        return declarations;
    };

    private DeclarationGenerator reverseVideoGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        declarations.add("-fx-fill: #" + this.toHexColor(this.getResolvedForegroundColor(context)));
        declarations.add("-fx-background-color: #" + this.toHexColor(this.getResolvedBackgroundColor(context)));
        return declarations;
    };

    private DeclarationGenerator visibilityGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        switch (newValue) {
            case TextAttributeValue.VISIBILITY_HIDDEN:
                declarations.add("visibility:hidden");//no -fx- prefix
            break;
            case TextAttributeValue.VISIBILITY_VISIBLE:
                declarations.add("visibility:visible");//no -fx- prefix
            break;
            default:
                throw new IllegalArgumentException("Unknown value=" + newValue);
        }
        return declarations;
    };

    private DeclarationGenerator strikethroughGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        switch (newValue) {
            case TextAttributeValue.STRIKETHROUGH_ON:
                declarations.add("-fx-strikethrough: true");//works only for text nodes
            break;
            case TextAttributeValue.STRIKETHROUGH_OFF:
                declarations.add("-fx-strikethrough: false");//works only for text nodes
            break;
            default:
                throw new IllegalArgumentException("Unknown value=" + newValue);
        }
        return declarations;
    };

    private DeclarationGenerator fontGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var font = (String) change.getNewValue().getValue();
        declarations.add("-fx-font-family: " + font);
        return declarations;
    };

    private DeclarationGenerator colorGenerator = (change, context) -> {
        var declarations = new ArrayList<String>();
        var newValue = (int) change.getNewValue().getValue();
        if (context.getNonDefaultValuesByAttribute().containsKey(TextAttribute.REVERSE_VIDEO)) {
            if (change.getAttribute()== TextAttribute.FOREGROUND_COLOR) {
                declarations.add("-fx-background-color: #" + this.toHexColor(newValue));
            } else if (change.getAttribute() == TextAttribute.BACKGROUND_COLOR) {
                declarations.add("-fx-fill: #" + this.toHexColor(newValue));
            }
        } else {
            if (change.getAttribute() == TextAttribute.FOREGROUND_COLOR) {
                declarations.add("-fx-fill: #" + this.toHexColor(newValue));
            } else if (change.getAttribute() == TextAttribute.BACKGROUND_COLOR) {
                declarations.add("-fx-background-color: #" + this.toHexColor(newValue));
            }
        }
        return declarations;
    };

    public JavaFxCssGenerator() {
        var map = this.getGeneratorsByAttribuiteKey();
        map.put(TextAttribute.WEIGHT, weightGenerator);
        map.put(TextAttribute.ITALIC, italicGenerator);
        map.put(TextAttribute.UNDERLINE, underlineGenerator);
//        map.put(TextAttribute.BLINKING, blinkingGenerator);
        map.put(TextAttribute.REVERSE_VIDEO, reverseVideoGenerator);
        map.put(TextAttribute.VISIBILITY, visibilityGenerator);
        map.put(TextAttribute.STRIKETHROUGH, strikethroughGenerator);
        map.put(TextAttribute.FONT, fontGenerator);
        map.put(TextAttribute.FOREGROUND_COLOR, colorGenerator);
        map.put(TextAttribute.BACKGROUND_COLOR, colorGenerator);
    }
}
