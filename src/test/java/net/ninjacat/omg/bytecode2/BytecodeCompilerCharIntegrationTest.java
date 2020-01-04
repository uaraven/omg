/*
 * omg: BytecodeCompilerIntegrationTest.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
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

package net.ninjacat.omg.bytecode2;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerCharIntegrationTest {

    @Test
    public void shouldMatchCharEq() {
        final Condition cond = Conditions.matcher().property("charProp").eq(432).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 432)), is(true));
        assertThat(matcher.matches(new TestClass((char) 431)), is(false));
    }

    @Test
    public void shouldMatchCharNeq() {
        final Condition cond = Conditions.matcher().property("charProp").neq(432).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 432)), is(false));
        assertThat(matcher.matches(new TestClass((char) 431)), is(true));
    }

    @Test
    public void shouldMatchCharLt() {
        final Condition cond = Conditions.matcher().property("charProp").lt(432).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 432)), is(false));
        assertThat(matcher.matches(new TestClass((char) 431)), is(true));
    }

    @Test
    public void shouldMatchCharGt() {
        final Condition cond = Conditions.matcher().property("charProp").gt(432).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 432)), is(false));
        assertThat(matcher.matches(new TestClass((char) 433)), is(true));
    }

    @Test
    public void shouldMatchCharOrInt() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("charProp").eq(432)
                        .property("charProp").eq(538))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 432)), is(true));
        assertThat(matcher.matches(new TestClass((char) 538)), is(true));
        assertThat(matcher.matches(new TestClass((char) 431)), is(false));
    }

    @Test
    public void shouldMatchCharIn() {
        final Condition cond = Conditions.matcher()
                .property("charProp").in(41, 42, 43, 44, 20124)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 41)), is(true));
        assertThat(matcher.matches(new TestClass((char) 20124)), is(true));
        assertThat(matcher.matches(new TestClass((char) 60)), is(false));
    }


    @Test
    public void shouldMatchCharInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("charProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((char) 41)), is(false));
        assertThat(matcher.matches(new TestClass((char) 8012454)), is(false));
        assertThat(matcher.matches(new TestClass((char) 60)), is(false));
    }

    public static class TestClass {
        private final char charProp;

        public TestClass(final char charProp) {
            this.charProp = charProp;
        }

        public char getCharProp() {
            return charProp;
        }
    }
}