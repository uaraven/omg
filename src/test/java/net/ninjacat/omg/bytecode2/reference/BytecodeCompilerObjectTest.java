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
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.Pattern;
import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytecodeCompilerObjectTest {

    @Test(expected = CompilerException.class)
    public void shouldFailEqMatch() {
        final Condition cond = Conditions.matcher().property("internalProp").eq(new Internal("abc")).build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        compiler.build(cond);
    }

    @Test
    public void shouldMatchStrRegex() {
        final Condition cond = Conditions.matcher()
                .property("internalProp").regex(".*a[bc]d.*")
                .build();

        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
        final Pattern<TestClass> matcher = compiler.build(cond);

        assertThat(matcher.matches(new TestClass("abd")), is(true));
        assertThat(matcher.matches(new TestClass("acd")), is(true));
        assertThat(matcher.matches(new TestClass("xyz")), is(false));
    }
//
//    @Test
//    public void shouldMatchStrIn() {
//        final Condition cond = Conditions.matcher()
//                .property("strProp").in("abc", "abd", "def")
//                .build();
//
//        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
//        final Pattern<TestClass> matcher = compiler.build(cond);
//
//        assertThat(matcher.matches(new TestClass("abd")), is(true));
//        assertThat(matcher.matches(new TestClass("abc")), is(true));
//        assertThat(matcher.matches(new TestClass("xyz")), is(false));
//    }
//
//    @Test
//    public void shouldMatchStrInEmpty() {
//        final Condition cond = Conditions.matcher()
//                .property("strProp").in()
//                .build();
//
//        final AsmPatternCompiler<TestClass> compiler = AsmPatternCompiler.forClass(TestClass.class);
//        final Pattern<TestClass> matcher = compiler.build(cond);
//
//        assertThat(matcher.matches(new TestClass("a")), is(false));
//        assertThat(matcher.matches(new TestClass("b")), is(false));
//        assertThat(matcher.matches(new TestClass("xyz")), is(false));
//    }

    public static class Internal {
        private final String strProp;

        public Internal(final String strProp) {
            this.strProp = strProp;
        }

        public String getStrProp() {
            return strProp;
        }


        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Internal internal = (Internal) o;
            return Objects.equals(strProp, internal.strProp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(strProp);
        }

        @Override
        public String toString() {
            return "Internal{" +
                    "strProp='" + strProp + '\'' +
                    '}';
        }
    }

    public static class TestClass {
        private final Internal internalProp;

        public TestClass(final String strProp) {
            this.internalProp = new Internal(strProp);
        }

        public Internal getInternalProp() {
            return internalProp;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final TestClass testClass = (TestClass) o;
            return Objects.equals(internalProp, testClass.internalProp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(internalProp);
        }
    }
}