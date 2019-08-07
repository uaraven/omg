package net.ninjacat.omg.reflect;

import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.List;

import static net.ninjacat.omg.CompilerSelectionStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LongCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(true));
        assertThat(pattern.matches(new LongTest(24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, SAFE).build(condition);

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


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<LongTest> pattern = PatternCompiler.forClass(LongTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(LongTest.class, SAFE).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(LongTest.class, SAFE).build(condition);
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