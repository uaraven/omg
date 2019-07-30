package net.ninjacat.omg.reflect;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class IntPatternTest {

    @Test
    public void testSimplePattern() {
        final Condition condition = Conditions.start()
                .property("longField").eq(42)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

        final List<TestClass> tests = List.of(new TestClass(0, (short) 0, 42L),
                new TestClass(0, (short) 0, 41L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0, (short) 0, 42L)));
    }

    @Test
    public void testOrPattern() {
        final Condition condition = Conditions.start()
                .or(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

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
    public void testAndPattern() {
        final Condition condition = Conditions.start()
                .and(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

        final List<TestClass> tests = List.of(new TestClass(0, (short) 0, 2L),
                new TestClass(1, (short) 0, -10L),
                new TestClass(1, (short) 0, 12L),
                new TestClass(0, (short) 0, 30L));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new TestClass(1, (short) 0, 12L)));

    }

    @Test
    public void testComplexPattern() {
        final Condition condition = Conditions.start()
                .property("shortField").neq(100)
                .and(cond -> cond
                        .property("intField").eq(1)
                        .or(orCond -> orCond
                                .property("longField").eq(10)
                                .property("longField").eq(20)
                        )
                )
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

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
    private static class TestClass {
        private int intField;
        private short shortField;
        private long longField;
    }
}