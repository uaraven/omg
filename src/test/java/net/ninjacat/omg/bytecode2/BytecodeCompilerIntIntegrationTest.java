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

public class BytecodeCompilerIntIntegrationTest {

    @Test
    public void shouldMatchSimpleInt() {
        final Condition cond = Conditions.matcher().property("intProp").eq(432).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432)), is(true));
        assertThat(matcher.matches(new TestClass(431)), is(false));
    }

    @Test
    public void shouldMatchIntOrInt() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("intProp").eq(432)
                        .property("intProp").eq(538))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432)), is(true));
        assertThat(matcher.matches(new TestClass(538)), is(true));
        assertThat(matcher.matches(new TestClass(431)), is(false));
    }

    @Test
    public void shouldMatchIntIn() {
        final Condition cond = Conditions.matcher()
                .property("intProp").in(41, 42, 43, 44, 8012454)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond, ImmutableCompilationOptions.builder().dumpToFile("/tmp/match_int_in.class").build());

        assertThat(matcher.matches(new TestClass(41)), is(true));
        assertThat(matcher.matches(new TestClass(8012454)), is(true));
        assertThat(matcher.matches(new TestClass(60)), is(false));
    }

    public static class TestClass {
        private final int intProp;

        public TestClass(final int intProp) {
            this.intProp = intProp;
        }

        public int getIntProp() {
            return intProp;
        }
    }
}