/*
 * omg: BytecodeCompilerFloatIntegrationTest.java
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

public class BytecodeCompilerFloatTest {

    @Test
    public void shouldMatchFloatEq() {
        final Condition cond = Conditions.matcher().property("floatProp").eq(4.0f).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.0f)), is(true));
        assertThat(matcher.matches(new TestClass(4.3f)), is(false));
    }


    @Test
    public void shouldMatchFloatNeq() {
        final Condition cond = Conditions.matcher().property("floatProp").neq(432.0f).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432.0f)), is(false));
        assertThat(matcher.matches(new TestClass(431.0f)), is(true));
    }

    @Test
    public void shouldMatchFloatLt() {
        final Condition cond = Conditions.matcher().property("floatProp").lt(43.2f).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(43.2f)), is(false));
        assertThat(matcher.matches(new TestClass(43.1f)), is(true));
    }

    @Test
    public void shouldMatchIntGt() {
        final Condition cond = Conditions.matcher().property("floatProp").gt(43.2f).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(43.2f)), is(false));
        assertThat(matcher.matches(new TestClass(43.3f)), is(true));
    }

    @Test
    public void shouldMatchFloatOrFloat() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("floatProp").eq(4.32f)
                        .property("floatProp").eq(5.38f))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.32f)), is(true));
        assertThat(matcher.matches(new TestClass(5.38f)), is(true));
        assertThat(matcher.matches(new TestClass(4.31f)), is(false));
    }

    @Test
    public void shouldMatchFloatIn() {
        final Condition cond = Conditions.matcher()
                .property("floatProp").in(4.1f, 4.2f, 4.3f, 4.4f, 8012454.0f)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.1f)), is(true));
        assertThat(matcher.matches(new TestClass(8012454f)), is(true));
        assertThat(matcher.matches(new TestClass(60f)), is(false));
    }


    @Test
    public void shouldMatchFloatInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("floatProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(4.1f)), is(false));
        assertThat(matcher.matches(new TestClass(8012454f)), is(false));
        assertThat(matcher.matches(new TestClass(60f)), is(false));
    }

    public static class TestClass {
        private final float floatProp;

        public TestClass(final float prop) {
            this.floatProp = prop;
        }

        public float getFloatProp() {
            return floatProp;
        }
    }
}