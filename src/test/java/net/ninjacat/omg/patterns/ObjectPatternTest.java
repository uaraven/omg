package net.ninjacat.omg.patterns;

import net.jcip.annotations.Immutable;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmgException;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@RunWith(Theories.class)
public class ObjectPatternTest {

    @Test
    @Theory
    public void testObjectEquality(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").eq(new InnerClass(1, "found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    @Test
    @Theory
    public void testObjectInequality(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").eq(new InnerClass(1, "found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "not found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(false));
    }

    @Test
    @Theory
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").match(obj -> obj.property("aString").eq("found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    @Test
    @Theory
    public void testNotPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .not(n -> n.property("inner").match(obj -> obj.property("aString").eq("found it")))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(false));
    }

    @Test
    @Theory
    public void testComplexPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").match(obj ->
                        obj.or(orCond -> orCond
                                .property("aString").eq("found it")
                                .property("aString").regex(".*works.*"))
                )
                .property("name").neq("Waldo")
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> testList =
                io.vavr.collection.List.of(
                        new TestClass(new InnerClass(1, "found it"), "Waldo"),
                        new TestClass(new InnerClass(1, "works!"), "Waldo"),
                        new TestClass(new InnerClass(1, "found it"), "Joseph"),
                        new TestClass(new InnerClass(1, "it works!"), "Yoda")
                ).asJava();

        final List<TestClass> matched = testList.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(matched, hasItems(
                new TestClass(new InnerClass(1, "found it"), "Joseph"),
                new TestClass(new InnerClass(1, "it works!"), "Yoda")
        ));
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailWhenUnsupportedCondition(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").gt(new InnerClass(1, "found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    @Immutable
    public static class InnerClass {
        private final int anInt;
        private final String aString;

        InnerClass(final int anInt, final String aString) {
            this.anInt = anInt;
            this.aString = aString;
        }

        public int getAnInt() {
            return anInt;
        }

        public String getAString() {
            return aString;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final InnerClass that = (InnerClass) o;
            return anInt == that.anInt &&
                    Objects.equals(aString, that.aString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(anInt, aString);
        }
    }


    public static class TestClass {
        private final InnerClass inner;
        private final String name;

        TestClass(final InnerClass inner, final String name) {
            this.inner = inner;
            this.name = name;
        }

        public InnerClass getInner() {
            return inner;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final TestClass testClass = (TestClass) o;
            return Objects.equals(inner, testClass.inner) &&
                    Objects.equals(name, testClass.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inner, name);
        }
    }
}
