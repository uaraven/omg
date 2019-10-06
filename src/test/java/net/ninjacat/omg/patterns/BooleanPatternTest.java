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
public class BooleanPatternTest {

    @Test
    @Theory
    public void testSimplePattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("boolField1").eq(true)
                .build();

        final Pattern<BoolPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(BoolPattern.class, strategy));

        final List<BoolPattern> tests = List.of(getTestClass(true),
                getTestClass(false));

        final java.util.List<BoolPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestClass(true)));
    }


    @Test
    @Theory
    public void testNotPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .not(n -> n.property("boolField1").eq(true))
                .build();

        final Pattern<BoolPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(BoolPattern.class, strategy));

        final List<BoolPattern> tests = List.of(getTestClass(true),
                getTestClass(false));

        final java.util.List<BoolPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getTestClass(false)));
    }

    @Test
    @Theory
    public void testOrPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(cond -> cond
                        .property("boolField1").eq(true)
                        .property("boolField2").eq(true))
                .build();

        final Pattern<BoolPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(BoolPattern.class, strategy));

        final List<BoolPattern> tests = List.of(getTestClass(true),
                getTestClass(true),
                getTestClass(false, false),
                getTestClass(false, true));

        final java.util.List<BoolPattern> result = tests.filter(pattern).asJava();

        assertThat(result, containsInAnyOrder(
                getTestClass(true),
                getTestClass(true),
                getTestClass(false, true)));

    }


    @Test
    @Theory
    public void testAndPattern(final CompilingStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(cond -> cond
                        .property("boolField1").eq(false)
                        .property("boolField2").eq(false))
                .build();

        final Pattern<BoolPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(BoolPattern.class, strategy));

        final List<BoolPattern> tests = List.of(getTestClass(true, true),
                getTestClass(true, false),
                getTestClass(false, true),
                getTestClass(false, false));

        final java.util.List<BoolPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getTestClass(false, false)));

    }


    private static BoolPattern getTestClass(final boolean b) {
        return ImmutableBoolPattern.of(b, !b);
    }

    private static BoolPattern getTestClass(final boolean b1, final boolean b2) {
        return ImmutableBoolPattern.of(b1, b2);
    }


    @Value.Immutable
    public interface BoolPattern {
        @Value.Parameter(order = 1)
        boolean getBoolField1();

        @Value.Parameter(order = 2)
        boolean getBoolField2();
    }
}