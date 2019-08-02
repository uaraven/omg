package net.ninjacat.omg.reflect;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FloatCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.EQ, 42);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(true));
        assertThat(pattern.matches(new FloatTest(24)), is(false));
    }


    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.GT, 42);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.LT, 42);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.MATCH, 42);

        AsmPatternCompiler.forClass(FloatTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.REGEX, 42);

        AsmPatternCompiler.forClass(FloatTest.class).build(condition);
    }

    private static PropertyCondition<Float> createPropertyCondition(final ConditionMethod method, final double value) {
        return new PropertyCondition<Float>() {

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
                return "floatField";
            }

            @Override
            public Float getValue() {
                return (float)value;
            }
        };
    }

    public static class FloatTest {
        private final float floatField;

        FloatTest(final float floatField) {
            this.floatField = floatField;
        }

        public float getFloatField() {
            return floatField;
        }
    }
}