package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.immutables.value.Value;
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
    public void testSimplePattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("longField").eq(42L)
                .build();

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(getTestClass(0, (short) 0, 42L),
                getTestClass(0, (short) 0, 41L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestClass(0, (short) 0, 42L)));
    }

    @Test
    @Theory
    public void testNotPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .not(n -> n.property("longField").eq(42L))
                .build();

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(getTestClass(0, (short) 0, 42L),
                getTestClass(0, (short) 0, 41L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestClass(0, (short) 0, 41L)));
    }

    @Test
    @Theory
    public void testSimplePatternTypeConversion(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("longField").eq(42)
                .build();

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(
                getTestClass(0, (short) 0, 42L),
                getTestClass(0, (short) 0, 41L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestClass(0, (short) 0, 42L)));
    }

    @Test
    @Theory
    public void testOrPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10L))
                .build();

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(getTestClass(0, (short) 0, 2L),
                getTestClass(1, (short) 0, -10L),
                getTestClass(1, (short) 0, 12L),
                getTestClass(0, (short) 0, 30L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                getTestClass(1, (short) 0, -10L),
                getTestClass(1, (short) 0, 12L),
                getTestClass(0, (short) 0, 30L)));

    }


    @Test
    @Theory
    public void testAndPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("intField").eq(1)
                        .property("longField").gt(10L))
                .build();

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(getTestClass(0, (short) 0, 2L),
                getTestClass(1, (short) 0, -10L),
                getTestClass(1, (short) 0, 12L),
                getTestClass(0, (short) 0, 30L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getTestClass(1, (short) 0, 12L)));

    }


    @Test
    @Theory
    public void testComplexPattern(final CompilingStrategy strategy) {
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

        final Pattern<IntPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(IntPattern.class, strategy));

        final List<IntPattern> tests = List.of(
                getTestClass(1, (short) 100, 10L),
                getTestClass(1, (short) 0, 2L),
                getTestClass(1, (short) 0, 20L),
                getTestClass(1, (short) 0, 10L),
                getTestClass(0, (short) 0, 10L));

        final java.util.List<IntPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getTestClass(1, (short) 0, 20L),
                getTestClass(1, (short) 0, 10L)));

    }

    private static IntPattern getTestClass(final int i, final short i2, final long l) {
        return ImmutableIntPattern.of(i, i2, l);
    }

    @Value.Immutable
    public interface IntPattern {
        @Value.Parameter
        int getIntField();

        @Value.Parameter
        short getShortField();

        @Value.Parameter
        long getLongField();
    }
}