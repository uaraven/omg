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
public class IntPatternTest {

    @Test
    @Theory
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("longField").eq(42L)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(new TestClass(0, (short) 0, 42L),
                new TestClass(0, (short) 0, 41L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0, (short) 0, 42L)));
    }


    @Test
    @Theory
    public void testSimplePatternTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("longField").eq(42)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(
                new TestClass(0, (short) 0, 42L),
                new TestClass(0, (short) 0, 41L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0, (short) 0, 42L)));
    }

    @Test
    @Theory
    public void testOrPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10L))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(new TestClass(0, (short) 0, 2L),
                new TestClass(1, (short) 0, -10L),
                new TestClass(1, (short) 0, 12L),
                new TestClass(0, (short) 0, 30L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                new TestClass(1, (short) 0, -10L),
                new TestClass(1, (short) 0, 12L),
                new TestClass(0, (short) 0, 30L)));

    }


    @Test
    @Theory
    public void testAndPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10L))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(new TestClass(0, (short) 0, 2L),
                new TestClass(1, (short) 0, -10L),
                new TestClass(1, (short) 0, 12L),
                new TestClass(0, (short) 0, 30L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new TestClass(1, (short) 0, 12L)));

    }


    @Test
    @Theory
    public void testComplexPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("shortField").neq((short) 100)
                .and(cond -> cond
                        .property("intField").eq(1)
                        .or(orCond -> orCond
                                .property("longField").eq(10L)
                                .property("longField").eq(20L)
                        )
                )
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> tests = List.of(
                new TestClass(1, (short) 100, 10L),
                new TestClass(1, (short) 0, 2L),
                new TestClass(1, (short) 0, 20L),
                new TestClass(1, (short) 0, 10L),
                new TestClass(0, (short) 0, 10L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new TestClass(1, (short) 0, 20L),
                new TestClass(1, (short) 0, 10L)));

    }

    @Value
    public static class TestClass {
        private int intField;
        private short shortField;
        private long longField;
    }
}