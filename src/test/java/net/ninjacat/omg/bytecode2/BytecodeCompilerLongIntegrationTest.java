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

public class BytecodeCompilerLongIntegrationTest {

    @Test
    public void shouldMatchLongEq() {
        final Condition cond = Conditions.matcher().property("longProp").eq(432L).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432L)), is(true));
        assertThat(matcher.matches(new TestClass(431L)), is(false));
    }

    @Test
    public void shouldMatchLongNeq() {
        final Condition cond = Conditions.matcher().property("longProp").neq(432L).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432L)), is(false));
        assertThat(matcher.matches(new TestClass(431L)), is(true));
    }

    @Test
    public void shouldMatchLongLt() {
        final Condition cond = Conditions.matcher().property("longProp").lt(432L).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432L)), is(false));
        assertThat(matcher.matches(new TestClass(431L)), is(true));
    }

    @Test
    public void shouldMatchLongGt() {
        final Condition cond = Conditions.matcher().property("longProp").gt(432L).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432L)), is(false));
        assertThat(matcher.matches(new TestClass(433L)), is(true));
    }

    @Test
    public void shouldMatchLongOrLong() {
        final Condition cond = Conditions.matcher()
                .or(or -> or.property("longProp").eq(432L)
                        .property("longProp").eq(538L))
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(432L)), is(true));
        assertThat(matcher.matches(new TestClass(538L)), is(true));
        assertThat(matcher.matches(new TestClass(431L)), is(false));
    }

    @Test
    public void shouldMatchLongIn() {
        final Condition cond = Conditions.matcher()
                .property("longProp").in(41L, 42L, 43L, 44L, 9998012454L)
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(41L)), is(true));
        assertThat(matcher.matches(new TestClass(9998012454L)), is(true));
        assertThat(matcher.matches(new TestClass(60L)), is(false));
    }


    @Test
    public void shouldMatchLongInEmpty() {
        final Condition cond = Conditions.matcher()
                .property("longProp").in()
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass(41L)), is(false));
        assertThat(matcher.matches(new TestClass(8012454L)), is(false));
        assertThat(matcher.matches(new TestClass(60L)), is(false));
    }

    public static class TestClass {
        private final long longProp;

        public TestClass(final long longProp) {
            this.longProp = longProp;
        }

        public long getLongProp() {
            return longProp;
        }
    }
}