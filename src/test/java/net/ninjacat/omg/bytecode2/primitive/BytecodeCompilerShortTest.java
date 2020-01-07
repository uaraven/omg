/*
 * omg: BytecodeCompilerShortIntegrationTest.java
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

package net.ninjacat.omg.bytecode2.primitive;

import net.ninjacat.omg.bytecode2.BytecodeConditionCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerShortTest {

    @Test
    public void shouldMatchShortEq() {
        final Condition cond = Conditions.matcher().property("shortProp").eq(432).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 432)), is(true));
        assertThat(matcher.matches(new TestClass((short) 431)), is(false));
    }

    @Test
    public void shouldMatchShortNeq() {
        final Condition cond = Conditions.matcher().property("shortProp").neq(432).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 432)), is(false));
        assertThat(matcher.matches(new TestClass((short) 431)), is(true));
    }

    @Test
    public void shouldMatchShortLt() {
        final Condition cond = Conditions.matcher().property("shortProp").lt(432).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 432)), is(false));
        assertThat(matcher.matches(new TestClass((short) 431)), is(true));
    }

    @Test
    public void shouldMatchShortGt() {
        final Condition cond = Conditions.matcher().property("shortProp").gt(432).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 432)), is(false));
        assertThat(matcher.matches(new TestClass((short) 433)), is(true));
    }

    @Test
    public void shouldMatchShortOrShort() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("shortProp").eq(432)
                        .property("shortProp").eq(538))
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 432)), is(true));
        assertThat(matcher.matches(new TestClass((short) 538)), is(true));
        assertThat(matcher.matches(new TestClass((short) 431)), is(false));
    }

    @Test
    public void shouldMatchShortIn() {
        final Condition cond = Conditions.matcher()
                .property("shortProp").in(-41, 42, 43, 44, 8012)
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) -41)), is(true));
        assertThat(matcher.matches(new TestClass((short) 8012)), is(true));
        assertThat(matcher.matches(new TestClass((short) 60)), is(false));
    }


    @Test
    public void shouldMatchShortInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("shortProp").in()
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((short) 41)), is(false));
        assertThat(matcher.matches(new TestClass((short) 20124)), is(false));
        assertThat(matcher.matches(new TestClass((short) 60)), is(false));
    }

    public static class TestClass {
        private final short shortProp;

        public TestClass(final short shortProp) {
            this.shortProp = shortProp;
        }

        public short getShortProp() {
            return shortProp;
        }
    }
}