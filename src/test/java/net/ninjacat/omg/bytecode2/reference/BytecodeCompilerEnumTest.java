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
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerEnumTest {

    @Test
    public void shouldMatchEnum() {
        final Condition cond = Conditions.matcher().property("enumProp").eq(Enum1.E11).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(false));
    }

    @Test
    public void shouldMatchNotEnum() {
        final Condition cond = Conditions.matcher().property("enumProp").neq(Enum1.E11).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(false));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(true));
    }


    @Test
    public void shouldMatchEnumOrEnum() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("enumProp").eq(Enum1.E11)
                        .property("enumProp").eq(Enum1.E12))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E13)), is(false));
    }

    @Test
    public void shouldMatchStrRegex() {
        final Condition cond = Conditions.matcher()
                .property("enumProp").regex("E1[12]")
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E13)), is(false));
    }

    @Test
    public void shouldMatchEnumIn() {
        final Condition cond = Conditions.matcher()
                .property("enumProp").in(Enum1.E11, Enum1.E12)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(true));
        assertThat(matcher.matches(new TestClass(Enum1.E13)), is(false));
    }


    @Test
    public void shouldMatchEnumInWithDifferentType() {
        final Condition cond = Conditions.matcher()
                .property("enumProp").in(Enum2.E21, Enum2.E22, Enum1.E13)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(false));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(false));
        assertThat(matcher.matches(new TestClass(Enum1.E13)), is(true));
    }

    @Test
    public void shouldMatchEnumInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("enumProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(Enum1.E11)), is(false));
        assertThat(matcher.matches(new TestClass(Enum1.E12)), is(false));
        assertThat(matcher.matches(new TestClass(Enum1.E13)), is(false));
    }

    public static class TestClass {
        private final Enum1 enumProp;

        public TestClass(final Enum1 enumProp) {
            this.enumProp = enumProp;
        }

        public Enum1 getEnumProp() {
            return enumProp;
        }
    }

    public enum Enum1 {
        E11, E12, E13
    }

    public enum Enum2 {
        E21, E22, E23
    }
}