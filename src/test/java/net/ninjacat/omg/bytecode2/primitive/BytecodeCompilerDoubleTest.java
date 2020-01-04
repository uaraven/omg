/*
 * omg: BytecodeCompilerDoubleIntegrationTest.java
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
import net.ninjacat.omg.bytecode2.ImmutableCompilationOptions;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerDoubleTest {

    @Test
    public void shouldMatchDoubleEq() {
        final Condition cond = Conditions.matcher().property("dblProp").eq(4.0).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond, ImmutableCompilationOptions.builder().dumpToFile("/tmp/test_double_eq.class").build());

        assertThat(matcher.matches(new TestClass(4.0)), is(true));
        assertThat(matcher.matches(new TestClass(4.3)), is(false));
    }


    @Test
    public void shouldMatchDoubleNeq() {
        final Condition cond = Conditions.matcher().property("dblProp").neq(432.0).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432.0)), is(false));
        assertThat(matcher.matches(new TestClass(431.0)), is(true));
    }

    @Test
    public void shouldMatchDoubleLt() {
        final Condition cond = Conditions.matcher().property("dblProp").lt(43.2).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(43.2)), is(false));
        assertThat(matcher.matches(new TestClass(43.1)), is(true));
    }

    @Test
    public void shouldMatchIntGt() {
        final Condition cond = Conditions.matcher().property("dblProp").gt(43.2).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(43.2)), is(false));
        assertThat(matcher.matches(new TestClass(43.3)), is(true));
    }

    @Test
    public void shouldMatchDoubleOrDouble() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("dblProp").eq(4.32)
                        .property("dblProp").eq(5.38))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.32)), is(true));
        assertThat(matcher.matches(new TestClass(5.38)), is(true));
        assertThat(matcher.matches(new TestClass(4.31)), is(false));
    }

    @Test
    public void shouldMatchDoubleIn() {
        final Condition cond = Conditions.matcher()
                .property("dblProp").in(4.1, 4.2, 4.3, 4.4, 8012454.0)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.1)), is(true));
        assertThat(matcher.matches(new TestClass(8012454)), is(true));
        assertThat(matcher.matches(new TestClass(60)), is(false));
    }


    @Test
    public void shouldMatchDoubleInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("dblProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.1)), is(false));
        assertThat(matcher.matches(new TestClass(8012454)), is(false));
        assertThat(matcher.matches(new TestClass(60)), is(false));
    }

    public static class TestClass {
        private final double dblProp;

        public TestClass(final double prop) {
            this.dblProp = prop;
        }

        public double getDblProp() {
            return dblProp;
        }
    }
}