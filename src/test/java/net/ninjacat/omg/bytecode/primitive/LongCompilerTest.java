package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LongCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.EQ, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(true));
        assertThat(pattern.matches(new LongTest(24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.GT, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.LT, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(new LongTest(42)), is(false));
        assertThat(pattern.matches(new LongTest(21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.MATCH, 42);

        AsmPatternCompiler.forClass(LongTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.REGEX, 42);

        AsmPatternCompiler.forClass(LongTest.class).build(condition);
    }

    private static PropertyCondition<Long> createPropertyCondition(final ConditionMethod method, final long value) {
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
                return value;
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