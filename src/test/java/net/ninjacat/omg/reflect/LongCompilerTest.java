package net.ninjacat.omg.reflect;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class LongCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, strategy).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(true));
        assertThat(pattern.matches(new LongTest(24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, strategy).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(24)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Long>> condition = new InCondition<>(
                "longField",
                io.vavr.collection.List.of(21L, 42L, 11L).asJava());


        final PropertyPattern<LongTest> pattern = ReflectPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(true));
        assertThat(pattern.matches(new LongTest(21)), is(true));
        assertThat(pattern.matches(new LongTest(84)), is(false));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, strategy).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, strategy).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(21)), is(true));
    }

    @Theory
    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(LongTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(LongTest.class, strategy).build(condition);
    }

    private static PropertyCondition<Long> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Long>() {

            @Override
            public String repr(final int level) {
                return "";
            }

            @Override
            public ConditionMethod getMethod() {
                return method;
            }

            @Override
            public String getProperty() {
                return "longField";
            }

            @Override
            public Long getValue() {
                return (long) 42;
            }
        };
    }

    public static class LongTest {
        private final long longField;

        LongTest(final long longField) {
            this.longField = longField;
        }

        public long getLongField() {
            return longField;
        }
    }
}