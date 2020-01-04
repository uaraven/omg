/*
 * omg: BytecodeCompilerBooleanTest.java
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

import net.ninjacat.omg.bytecode2.AsmPatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerBooleanTest {

    @Test
    public void shouldMatchBooleanEq() {
        final Condition cond = Conditions.matcher().property("boolProp").eq(true).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(true)), is(true));
        assertThat(matcher.matches(new TestClass(false)), is(false));
    }

    @Test
    public void shouldMatchIntNeq() {
        final Condition cond = Conditions.matcher().property("boolProp").neq(true).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(true)), is(false));
        assertThat(matcher.matches(new TestClass(false)), is(true));
    }

    public static class TestClass {
        private final boolean boolProp;

        public TestClass(final boolean boolProp) {
            this.boolProp = boolProp;
        }

        public boolean getBoolProp() {
            return boolProp;
        }
    }
}