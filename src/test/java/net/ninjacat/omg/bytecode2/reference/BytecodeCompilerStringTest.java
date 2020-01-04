/*
 * omg: BytecodeCompilerStringIntegrationTest.java
 *
 * Copyright 2020 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.bytecode2.reference;

import net.ninjacat.omg.bytecode2.AsmPatternCompiler;
import net.ninjacat.omg.bytecode2.ImmutableCompilationOptions;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerStringTest {

    @Test
    public void shouldMatchSimpleStr() {
        final Condition cond = Conditions.matcher().property("strProp").eq("abc").build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("abc")), is(true));
        assertThat(matcher.matches(new TestClass("xyz")), is(false));
    }

    @Test
    public void shouldMatchNotStr() {
        final Condition cond = Conditions.matcher().property("strProp").neq("abc").build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("abc")), is(false));
        assertThat(matcher.matches(new TestClass("xyz")), is(true));
    }


    @Test
    public void shouldMatchStrOrStr() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("strProp").eq("abc")
                        .property("strProp").eq("def"))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("abc")), is(true));
        assertThat(matcher.matches(new TestClass("def")), is(true));
        assertThat(matcher.matches(new TestClass("xyz")), is(false));
    }

    @Test
    public void shouldMatchStrRegex() {
        final Condition cond = Conditions.matcher()
                .property("strProp").regex("a[bc]d")
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond, ImmutableCompilationOptions.builder().dumpToFile("/tmp/match_str_regex.class").build());

        assertThat(matcher.matches(new TestClass("abd")), is(true));
        assertThat(matcher.matches(new TestClass("acd")), is(true));
        assertThat(matcher.matches(new TestClass("abc")), is(false));
    }

    @Test
    public void shouldMatchStrIn() {
        final Condition cond = Conditions.matcher()
                .property("strProp").in("abc", "abd", "def")
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("abd")), is(true));
        assertThat(matcher.matches(new TestClass("abc")), is(true));
        assertThat(matcher.matches(new TestClass("xyz")), is(false));
    }

    @Test
    public void shouldMatchStrInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("strProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("a")), is(false));
        assertThat(matcher.matches(new TestClass("b")), is(false));
        assertThat(matcher.matches(new TestClass("xyz")), is(false));
    }

    public static class TestClass {
        private final String strProp;

        public TestClass(final String strProp) {
            this.strProp = strProp;
        }

        public String getStrProp() {
            return strProp;
        }
    }
}