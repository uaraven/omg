package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
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
public class DoublePatternTest {

    @Test
    @Theory
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("doubleField").eq(42.0)
                .build();

        final Pattern<DoublePattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(DoublePattern.class, strategy));

        final List<DoublePattern> tests = List.of(getTestPattern(0f, 42.0),
                getTestPattern(0f, 41.0));

        final java.util.List<DoublePattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestPattern(0f, 42.0)));
    }

    @Test
    @Theory
    public void testNotPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .not(n -> n.property("doubleField").eq(42.0))
                .build();


        final Pattern<DoublePattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(DoublePattern.class, strategy));

        final List<DoublePattern> tests = List.of(getTestPattern(0f, 42.0),
                getTestPattern(0f, 41.0));

        final java.util.List<DoublePattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestPattern(0f, 41.0)));
    }


    @Test
    @Theory
    public void testOrPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("floatField").eq(1.0f)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<DoublePattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(DoublePattern.class, strategy));

        final List<DoublePattern> tests = List.of(getTestPattern(0, 2.0),
                getTestPattern(1.0f, -10.0),
                getTestPattern(1f, 12.0),
                getTestPattern(0, 30.0));

        final java.util.List<DoublePattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                getTestPattern(1, -10.0),
                getTestPattern(1, 12.0),
                getTestPattern(0, 30.0)));

    }

    @Test
    @Theory
    public void testAndPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("floatField").eq(1.0f)
                        .property("doubleField").gt(10.0))
                .build();

        final Pattern<DoublePattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(DoublePattern.class, strategy));

        final List<DoublePattern> tests = List.of(
                getTestPattern(0, 2.0),
                getTestPattern(1, -10.0),
                getTestPattern(1, 12.0),
                getTestPattern(0, 30.0));

        final java.util.List<DoublePattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getTestPattern(1, 12.0)));
    }


    @Test
    @Theory
    public void testSimplePatternTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("doubleField").eq(42f)
                .build();

        final Pattern<DoublePattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(DoublePattern.class, strategy));

        final List<DoublePattern> tests = List.of(
                getTestPattern(0, 42.0),
                getTestPattern(0, 41.0));

        final java.util.List<DoublePattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestPattern(0, 42.0)));
    }

    private static DoublePattern getTestPattern(final float i, final double v) {
        return ImmutableDoublePattern.of(i, v);
    }

    @Value.Immutable
    @Value.Style(allParameters = true)
    public interface DoublePattern {
        float getFloatField();

        double getDoubleField();
    }
}