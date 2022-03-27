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
package pk.ansi4j.core.iso6429;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction;

/**
 *
 * @author Pavel Kastornyy
 */
public class ControlSequenceParserTest {

    private static ControlSequenceParser parser;

    @BeforeAll
    public static void init() {
        parser = new ControlSequenceParser();
    }

    @Test
    public void parseArguments_string_noArg_success() {
        var arguments = parser.parseArguments("");
        assertThat(arguments, nullValue());
    }

    @Test
    public void parseArguments_string_oneArgNoDefaultValues_success() {
        var arguments = parser.parseArguments("20");
        assertThat(arguments, equalTo(List.of("20")));
    }

    @Test
    public void parseArguments_string_manyArgsWithoutDefaultValues_success() {
        var arguments = parser.parseArguments("20;30");
        assertThat(arguments, equalTo(List.of("20", "30")));
    }

    @Test
    public void parseArguments_string_manyArgsWithOneDefaultValues_success() {
        var arguments = parser.parseArguments(";20;30");
        assertThat(arguments, equalTo(Arrays.asList(null, "20", "30")));
    }

    @Test
    public void parseArguments_string_manyArgsWithTwoDefaultValues_success() {
        var arguments = parser.parseArguments("20;;30;");
        assertThat(arguments, equalTo(Arrays.asList("20", null, "30", null)));
    }

    @Test
    public void parseArguments_string_manyArgsWithThreeDefaultValues_success() {
        var arguments = parser.parseArguments(";20;foo;;30;");
        assertThat(arguments, equalTo(Arrays.asList(null, "20", "foo", null, "30", null)));
    }

    @Test
    public void parseArguments_string_manyArgsWithFourDefaultValues_success() {
        var arguments = parser.parseArguments(";20;foo;;;30;");
        assertThat(arguments, equalTo(Arrays.asList(null, "20", "foo", null, null, "30", null)));
    }

}
