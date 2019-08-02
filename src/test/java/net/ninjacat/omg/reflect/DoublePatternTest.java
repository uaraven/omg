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

public class DoublePatternTest {

    @Test
    public void testSimplePattern() {
        final Condition condition = Conditions.matcher()
                .property("doubleField").eq(42.0)
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

        final List<TestClass> tests = List.of(new TestClass(0, 42.0),
                new TestClass(0, 41.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new TestClass(0, 42.0)));
    }

    @Test
    public void testOrPattern() {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("floatField").eq(1.0)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

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
    public void testAndPattern() {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("floatField").eq(1.0)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

        final List<TestClass> tests = List.of(
                new TestClass(0, 2.0),
                new TestClass(1, -10.0),
                new TestClass(1, 12.0),
                new TestClass(0, 30.0));

        final java.util.List<TestClass> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new TestClass(1, 12.0)));

    }

    @Value
    private static class TestClass {
        private float floatField;
        private double doubleField;
    }
}