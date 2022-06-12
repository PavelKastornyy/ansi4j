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
package pk.ansi4j.core.it;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import pk.ansi4j.core.DefaultFunctionFinder;
import pk.ansi4j.core.api.ParserFactory;
import pk.ansi4j.core.DefaultParserFactory;
import pk.ansi4j.core.DefaultTextHandler;
import pk.ansi4j.core.api.Fragment;
import pk.ansi4j.core.api.TextFragment;
import pk.ansi4j.core.api.utils.Characters;
import pk.ansi4j.core.iso6429.ControlSequenceHandler;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
import pk.ansi4j.core.api.FragmentType;
import pk.ansi4j.core.api.FunctionFragment;
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction;
import pk.ansi4j.core.api.Environment;
import pk.ansi4j.core.api.iso6429.C0ControlFunction;
import pk.ansi4j.core.api.iso6429.C1ControlFunction;
import pk.ansi4j.core.api.iso6429.IndependentControlFunction;
import pk.ansi4j.core.iso6429.C0ControlFunctionHandler;
import pk.ansi4j.core.iso6429.C1ControlFunctionHandler;
import pk.ansi4j.core.iso6429.ControlStringHandler;
import pk.ansi4j.core.iso6429.IndependentControlFunctionHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.ansi4j.core.api.Parser;
import pk.ansi4j.core.api.StreamParser;
/**
 *
 * @author Pavel Kastornyy
 */
public class DefaultParserIT {

    private static final Logger logger = LoggerFactory.getLogger(DefaultParserIT.class);

    private static final String _7_BIT_PARSER_PROVIDER = "provide7BitParsers";

    private static final String _8_BIT_PARSER_PROVIDER = "provide8BitParsers";

    @FunctionalInterface
    private static interface ParserProvider {

        Parser provide(String text);
    }

    private static ParserFactory factory7Bit;

    private static ParserFactory factory8Bit;

    @BeforeAll
    public static void init() {
        factory7Bit = new DefaultParserFactory.Builder()
            .environment(Environment._7_BIT)
            .textHandler(new DefaultTextHandler())
            .functionFinder(new DefaultFunctionFinder())
            .functionHandlers(new C0ControlFunctionHandler(),
                    new C1ControlFunctionHandler(),
                    new ControlSequenceHandler(),
                    new IndependentControlFunctionHandler(),
                    new ControlStringHandler())
            .build();

        factory8Bit = new DefaultParserFactory.Builder()
            .environment(Environment._8_BIT)
            .textHandler(new DefaultTextHandler())
            .functionFinder(new DefaultFunctionFinder())
            .functionHandlers(new C0ControlFunctionHandler(),
                    new C1ControlFunctionHandler(),
                    new ControlSequenceHandler(),
                    new IndependentControlFunctionHandler(),
                    new ControlStringHandler())
            .build();
    }

    protected static List<ParserProvider> provide7BitParsers() {
        return List.of((text) -> factory7Bit.createParser(text),
                        (text) -> factory7Bit.createParser(
                                new ByteArrayInputStream(text.getBytes()),
                                StandardCharsets.UTF_8,
                                1024));
    }

    protected static List<ParserProvider> provide8BitParsers() {
        return List.of((text) -> factory8Bit.createParser(text),
                        (text) -> factory8Bit.createParser(
                                new ByteArrayInputStream(text.getBytes()),
                                StandardCharsets.UTF_8,
                                1024));
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_textFnTextFn_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 [main] \u001b[33;1m[WARN]\u001b[5;R abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(5));

        TextFragment f0 = (TextFragment) fragments.get(0);
        assertThat(f0.getType(), equalTo(FragmentType.TEXT));
        var f0Text = "2022-03-14 02:32:24.130 [main] ";
        assertThat(f0.getText(), equalTo(f0Text));
        assertThat(text.substring(f0.getStartIndex(), f0.getEndIndex()), equalTo(f0Text));

        FunctionFragment f1 = (FunctionFragment) fragments.get(1);
        this.checkMFunctionFragment(text, f1);

        TextFragment f2 = (TextFragment) fragments.get(2);
        assertThat(f2.getType(), equalTo(FragmentType.TEXT));
        var f2Text = "[WARN]";
        assertThat(f2.getText(), equalTo(f2Text));
        assertThat(text.substring(f2.getStartIndex(), f2.getEndIndex()), equalTo(f2Text));

        FunctionFragment f3 = (FunctionFragment) fragments.get(3);
        this.checkRFunctionFragment(text, f3);

        TextFragment f4 = (TextFragment) fragments.get(4);
        assertThat(f4.getType(), equalTo(FragmentType.TEXT));
        var f4Text = " abc.def.0123.ghi";
        assertThat(f4.getText(), equalTo(f4Text));
        assertThat(text.substring(f4.getStartIndex(), f4.getEndIndex()), equalTo(f4Text));
        this.closeParser(parser);
    }


    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_FnTextFn_success(ParserProvider parserProvider) {
        var text = "\u001b[33;1m2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi\u001b[5;R";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(3));

        FunctionFragment f0 = (FunctionFragment) fragments.get(0);
        this.checkMFunctionFragment(text, f0);

        TextFragment f1 = (TextFragment) fragments.get(1);
        assertThat(f1.getType(), equalTo(FragmentType.TEXT));
        var f1Text = "2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi";
        assertThat(f1.getText(), equalTo(f1Text));
        assertThat(text.substring(f1.getStartIndex(), f1.getEndIndex()), equalTo(f1Text));

        FunctionFragment f2 = (FunctionFragment) fragments.get(2);
        this.checkRFunctionFragment(text, f2);
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_FnFnText_success(ParserProvider parserProvider) {
        var text = "\u001b[33;1m\u001b[5;R2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(3));

        FunctionFragment f0 = (FunctionFragment) fragments.get(0);
        this.checkMFunctionFragment(text, f0);

        FunctionFragment f1 = (FunctionFragment) fragments.get(1);
        this.checkRFunctionFragment(text, f1);

        TextFragment f2 = (TextFragment) fragments.get(2);
        assertThat(f2.getType(), equalTo(FragmentType.TEXT));
        var f2Text = "2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi";
        assertThat(f2.getText(), equalTo(f2Text));
        assertThat(text.substring(f2.getStartIndex(), f2.getEndIndex()), equalTo(f2Text));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void nparse_textFnFn_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi\u001b[33;1m\u001b[5;R";

        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(3));

        TextFragment f0 = (TextFragment) fragments.get(0);
        assertThat(f0.getType(), equalTo(FragmentType.TEXT));
        var f0Text = "2022-03-14 02:32:24.130 [main] [WARN] abc.def.0123.ghi";
        assertThat(f0.getText(), equalTo(f0Text));
        assertThat(text.substring(f0.getStartIndex(), f0.getEndIndex()), equalTo(f0Text));

        FunctionFragment f1 = (FunctionFragment) fragments.get(1);
        this.checkMFunctionFragment(text, f1);

        FunctionFragment f2 = (FunctionFragment) fragments.get(2);
        this.checkRFunctionFragment(text, f2);
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_defaultArgument_success(ParserProvider parserProvider) {
        var text = "\u001b[m";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(1));
        var f0 = (FunctionFragment) fragments.get(0);
        assertThat(f0.getArguments(), notNullValue());
        assertThat(f0.getArguments(), hasSize(1));
        assertThat(f0.getArguments().get(0).getValue(), equalTo(0));
        assertThat(f0.getArguments().get(0).isDefault(), equalTo(true));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_c0In7bitEnv_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 \u000B[main] \u000e [WARN] \u000f abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.VT_LINE_TABULATION));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000B"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.SO_SHIFT_OUT));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000e"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.SI_SHIFT_IN));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000f"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_8_BIT_PARSER_PROVIDER)
    public void parse_c0In8bitEnv_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 \u000B[main] \u000e [WARN] \u000f abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.VT_LINE_TABULATION));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000B"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.LS1_LOCKING_SHIFT_ONE));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000e"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(C0ControlFunction.LS0_LOCKING_SHIFT_ZERO));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u000f"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_c1In7bitEnv_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 \u001bG[main] \u001bV [WARN] \u001bQ abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.ESA_END_OF_SELECTED_AREA));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001bG"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.SPA_START_OF_GUARDED_AREA));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001bV"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.PU1_PRIVATE_USE_ONE));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001bQ"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_8_BIT_PARSER_PROVIDER)
    public void parse_c1In8bitEnv_success(ParserProvider parserProvider) {
        var text = "2022-03-14 02:32:24.130 \u0087[main] \u0096 [WARN] \u0091 abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.ESA_END_OF_SELECTED_AREA));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u0087"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.SPA_START_OF_GUARDED_AREA));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u0096"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.PU1_PRIVATE_USE_ONE));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u0091"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_independentIn7bitEnv_success(ParserProvider parserProvider) {
         var text = "2022-03-14 02:32:24.130 \u001bo[main] \u001b| [WARN] \u001b` abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.LS3_LOCKING_SHIFT_THREE));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001bo"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.LS3R_LOCKING_SHIFT_THREE_RIGHT));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001b|"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.DMI_DISABLE_MANUAL_INPUT));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001b`"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_8_BIT_PARSER_PROVIDER)
    public void parse_independentIn8bitEnv_success(ParserProvider parserProvider) {
         var text = "2022-03-14 02:32:24.130 \u001bo[main] \u001b| [WARN] \u001b` abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("2022-03-14 02:32:24.130 "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.LS3_LOCKING_SHIFT_THREE));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001bo"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo("[main] "));

        ff = (FunctionFragment) fragments.get(3);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.LS3R_LOCKING_SHIFT_THREE_RIGHT));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001b|"));

        ft = (TextFragment) fragments.get(4);
        assertThat(ft.getText(), equalTo(" [WARN] "));

        ff = (FunctionFragment) fragments.get(5);
        assertThat(ff.getFunction(), equalTo(IndependentControlFunction.DMI_DISABLE_MANUAL_INPUT));
        assertThat(ff.getArguments(), hasSize(0));
        assertThat(ff.getText(), equalTo("\u001b`"));

        ft = (TextFragment) fragments.get(6);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_7_BIT_PARSER_PROVIDER)
    public void parse_controlStringIn7bitEnv_success(ParserProvider parserProvider) {
         var text = "one two three \u001b]4;6;some text\u001b\\ abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(3));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("one two three "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.OSC_OPERATING_SYSTEM_COMMAND));
        assertThat(ff.getArguments(), hasSize(3));
        assertThat(ff.getText(), equalTo("\u001b]4;6;some text\u001b\\"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @ParameterizedTest
    @MethodSource(_8_BIT_PARSER_PROVIDER)
    public void parse_controlStringIn8bitEnv_success(ParserProvider parserProvider) {
         var text = "one two three \u009d4;6;some text\u009c abc.def.0123.ghi";
        var parser = parserProvider.provide(text);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(3));

        TextFragment ft = (TextFragment) fragments.get(0);
        assertThat(ft.getText(), equalTo("one two three "));

        FunctionFragment ff = (FunctionFragment) fragments.get(1);
        assertThat(ff.getFunction(), equalTo(C1ControlFunction.OSC_OPERATING_SYSTEM_COMMAND));
        assertThat(ff.getArguments(), hasSize(3));
        assertThat(ff.getText(), equalTo("\u009d4;6;some text\u009c"));

        ft = (TextFragment) fragments.get(2);
        assertThat(ft.getText(), equalTo(" abc.def.0123.ghi"));
        this.closeParser(parser);
    }

    @Test
    public void parse_controlStringIn7bitEnvAsStream_success() {
        var text = "one two three \u001b]4;6;some text\u001b\\ abc.def.0123.ghi";
        var parser = factory7Bit.createParser(new ByteArrayInputStream(text.getBytes()), StandardCharsets.UTF_8, 6);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(7));

        TextFragment f0 = (TextFragment) fragments.get(0);
        assertThat(f0.getText(), equalTo("one tw"));
        assertThat(text.substring(f0.getStartIndex(), f0.getEndIndex()), equalTo(f0.getText()));

        TextFragment f1 = (TextFragment) fragments.get(1);
        assertThat(f1.getText(), equalTo("o thre"));
        assertThat(text.substring(f1.getStartIndex(), f1.getEndIndex()), equalTo(f1.getText()));

        TextFragment f2 = (TextFragment) fragments.get(2);
        assertThat(f2.getText(), equalTo("e "));
        assertThat(text.substring(f2.getStartIndex(), f2.getEndIndex()), equalTo(f2.getText()));

        FunctionFragment f3 = (FunctionFragment) fragments.get(3);
        assertThat(f3.getFunction(), equalTo(C1ControlFunction.OSC_OPERATING_SYSTEM_COMMAND));
        assertThat(f3.getArguments(), hasSize(3));
        assertThat(f3.getText(), equalTo("\u001b]4;6;some text\u001b\\"));
        assertThat(text.substring(f3.getStartIndex(), f3.getEndIndex()), equalTo(f3.getText()));

        TextFragment f4 = (TextFragment) fragments.get(4);
        assertThat(f4.getText(), equalTo(" abc."));// "\\" escaping character is used
        assertThat(text.substring(f4.getStartIndex(), f4.getEndIndex()), equalTo(f4.getText()));

        TextFragment f5 = (TextFragment) fragments.get(5);
        assertThat(f5.getText(), equalTo("def.01"));
        assertThat(text.substring(f5.getStartIndex(), f5.getEndIndex()), equalTo(f5.getText()));

        TextFragment f6 = (TextFragment) fragments.get(6);
        assertThat(f6.getText(), equalTo("23.ghi"));
        assertThat(text.substring(f6.getStartIndex(), f6.getEndIndex()), equalTo(f6.getText()));
        this.closeParser(parser);
    }

    @Test
    public void parse_controlStringIn8bitEnvAsStream_success() {
         var text = "one two three \u009d4;6;some text\u009c abc.def.0123.ghi";
        var parser = factory8Bit.createParser(new ByteArrayInputStream(text.getBytes()), StandardCharsets.UTF_8, 6);
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        while ((fragment = parser.parse()) != null) {
            fragments.add(fragment);
        }
        assertThat(fragments, hasSize(8));

        TextFragment f0 = (TextFragment) fragments.get(0);
        assertThat(f0.getText(), equalTo("one tw"));
        assertThat(text.substring(f0.getStartIndex(), f0.getEndIndex()), equalTo(f0.getText()));

        TextFragment f1 = (TextFragment) fragments.get(1);
        assertThat(f1.getText(), equalTo("o thre"));
        assertThat(text.substring(f1.getStartIndex(), f1.getEndIndex()), equalTo(f1.getText()));

        TextFragment f2 = (TextFragment) fragments.get(2);
        assertThat(f2.getText(), equalTo("e "));
        assertThat(text.substring(f2.getStartIndex(), f2.getEndIndex()), equalTo(f2.getText()));

        FunctionFragment f3 = (FunctionFragment) fragments.get(3);
        assertThat(f3.getFunction(), equalTo(C1ControlFunction.OSC_OPERATING_SYSTEM_COMMAND));
        assertThat(f3.getArguments(), hasSize(3));
        assertThat(f3.getText(), equalTo("\u009d4;6;some text\u009c"));
        assertThat(text.substring(f3.getStartIndex(), f3.getEndIndex()), equalTo(f3.getText()));

        TextFragment f4 = (TextFragment) fragments.get(4);
        assertThat(f4.getText(), equalTo(" "));// "\\" escaping character is used
        assertThat(text.substring(f4.getStartIndex(), f4.getEndIndex()), equalTo(f4.getText()));

        TextFragment f5 = (TextFragment) fragments.get(5);
        assertThat(f5.getText(), equalTo("abc.de"));
        assertThat(text.substring(f5.getStartIndex(), f5.getEndIndex()), equalTo(f5.getText()));

        TextFragment f6 = (TextFragment) fragments.get(6);
        assertThat(f6.getText(), equalTo("f.0123"));
        assertThat(text.substring(f6.getStartIndex(), f6.getEndIndex()), equalTo(f6.getText()));

        TextFragment f7 = (TextFragment) fragments.get(7);
        assertThat(f7.getText(), equalTo(".ghi"));
        assertThat(text.substring(f7.getStartIndex(), f7.getEndIndex()), equalTo(f7.getText()));
        this.closeParser(parser);
    }

    private void checkMFunctionFragment(String text, FunctionFragment mFragment) {
        assertThat(mFragment.getType(), equalTo(FragmentType.FUNCTION));
        var mFragmentText = Characters.ESC + "[33;1m";
        assertThat(mFragment.getText(), equalTo(mFragmentText));
        assertThat(text.substring(mFragment.getStartIndex(), mFragment.getEndIndex()), equalTo(mFragmentText));
        assertThat(mFragment.getFunction(), equalTo(ControlSequenceFunction.SGR_SELECT_GRAPHIC_RENDITION));
        assertThat(mFragment.getArguments().size(), equalTo(2));
        assertThat(mFragment.getArguments().get(0).getValue(), equalTo(33));
        assertThat(mFragment.getArguments().get(0).isDefault(), equalTo(false));
        assertThat(mFragment.getArguments().get(1).getValue(), equalTo(1));
        assertThat(mFragment.getArguments().get(1).isDefault(), equalTo(false));
    }

    private void checkRFunctionFragment(String text, FunctionFragment rFragment) {
        assertThat(rFragment.getType(), equalTo(FragmentType.FUNCTION));
        var rFragmentText = Characters.ESC + "[5;R";
        assertThat(rFragment.getText(), equalTo(rFragmentText));
        assertThat(text.substring(rFragment.getStartIndex(), rFragment.getEndIndex()), equalTo(rFragmentText));
        assertThat(rFragment.getFunction(), equalTo(ControlSequenceFunction.CPR_ACTIVE_POSITION_REPORT));
        assertThat(rFragment.getArguments().size(), equalTo(2));
        assertThat(rFragment.getArguments().get(0).getValue(), equalTo(5));
        assertThat(rFragment.getArguments().get(0).isDefault(), equalTo(false));
        assertThat(rFragment.getArguments().get(1).getValue(), equalTo(1));
        assertThat(rFragment.getArguments().get(1).isDefault(), equalTo(true));
    }

    private void closeParser(Parser parser) {
        if (parser instanceof StreamParser) {
            var streamParser = (StreamParser) parser;
            try {
                streamParser.close();
            } catch (IOException ex) {
                logger.error("Error closing stream parser", ex);
            }
        }
    }
}


