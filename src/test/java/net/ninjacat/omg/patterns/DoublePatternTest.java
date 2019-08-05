package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Theories.class)
public class DoublePatternTest {

    @Test
    @Theory
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("doubleField").eq(42.0)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(new TestClass(0f, 42.0),
                new TestClass(0f, 41.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0f, 42.0)));
    }

    @Test
    @Theory
    public void testOrPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("floatField").eq(1.0f)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(new TestClass(0, 2.0),
                new TestClass(1.0f, -10.0),
                new TestClass(1f, 12.0),
                new TestClass(0, 30.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                new TestClass(1, -10.0),
                new TestClass(1, 12.0),
                new TestClass(0, 30.0)));

    }

    @Test
    @Theory
    public void testAndPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("floatField").eq(1.0f)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(
                new TestClass(0, 2.0),
                new TestClass(1, -10.0),
                new TestClass(1, 12.0),
                new TestClass(0, 30.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new TestClass(1, 12.0)));
    }


    @Test
    @Theory
    public void testSimplePatternTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("doubleField").eq(42f)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(
                new TestClass(0, 42.0),
                new TestClass(0, 41.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0, 42.0)));
    }

    @Value
    public static class TestClass {
        private float floatField;
        private double doubleField;
    }
}