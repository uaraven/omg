/*
 * omg: BytecodeCompilerByteIntegrationTest.java
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

import net.ninjacat.omg.bytecode2.BytecodeConditionCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerByteTest {

    @Test
    public void shouldMatchByteEq() {
        final Condition cond = Conditions.matcher().property("byteProp").eq(32).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 32)), is(true));
        assertThat(matcher.matches(new TestClass((byte) 31)), is(false));
    }

    @Test
    public void shouldMatchByteNeq() {
        final Condition cond = Conditions.matcher().property("byteProp").neq(43).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 43)), is(false));
        assertThat(matcher.matches(new TestClass((byte) 42)), is(true));
    }

    @Test
    public void shouldMatchByteLt() {
        final Condition cond = Conditions.matcher().property("byteProp").lt(42).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 42)), is(false));
        assertThat(matcher.matches(new TestClass((byte) 41)), is(true));
    }

    @Test
    public void shouldMatchByteGt() {
        final Condition cond = Conditions.matcher().property("byteProp").gt(42).build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 42)), is(false));
        assertThat(matcher.matches(new TestClass((byte) 43)), is(true));
    }

    @Test
    public void shouldMatchByteOrByte() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("byteProp").eq(42)
                        .property("byteProp").eq(53))
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 42)), is(true));
        assertThat(matcher.matches(new TestClass((byte) 53)), is(true));
        assertThat(matcher.matches(new TestClass((byte) 43)), is(false));
    }

    @Test
    public void shouldMatchByteIn() {
        final Condition cond = Conditions.matcher()
                .property("byteProp").in(41, 42, 43, 44, -124)
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 41)), is(true));
        assertThat(matcher.matches(new TestClass((byte) -124)), is(true));
        assertThat(matcher.matches(new TestClass((byte) 60)), is(false));
    }


    @Test
    public void shouldMatchByteInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("byteProp").in()
                .build();

        final BytecodeConditionCompiler<TestClass> compiler = BytecodeConditionCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass((byte) 41)), is(false));
        assertThat(matcher.matches(new TestClass((byte) 124)), is(false));
        assertThat(matcher.matches(new TestClass((byte) 60)), is(false));
    }

    public static class TestClass {
        private final Byte byteProp;

        public TestClass(final Byte byteProp) {
            this.byteProp = byteProp;
        }

        public Byte getByteProp() {
            return byteProp;
        }
    }
}