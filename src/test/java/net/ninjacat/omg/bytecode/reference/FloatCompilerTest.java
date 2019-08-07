package net.ninjacat.omg.bytecode.reference;

import io.vavr.collection.List;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FloatCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.EQ, 42f);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42f)), is(true));
        assertThat(pattern.matches(new FloatTest(24f)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.NEQ, 42f);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42f)), is(false));
        assertThat(pattern.matches(new FloatTest(24f)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.GT, 42f);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42f)), is(false));
        assertThat(pattern.matches(new FloatTest(84f)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.LT, 42f);

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42f)), is(false));
        assertThat(pattern.matches(new FloatTest(21f)), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final InCondition<Float> condition = new InCondition<>("floatField", List.of(42f, 43f).asJava());

        final PropertyPattern<FloatTest> pattern = AsmPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42f)), is(true));
        assertThat(pattern.matches(new FloatTest(21f)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.MATCH, 42f);

        AsmPatternCompiler.forClass(FloatTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.REGEX, 42f);

        AsmPatternCompiler.forClass(FloatTest.class).build(condition);
    }

    private static PropertyCondition<Float> createPropertyCondition(final ConditionMethod method, final Float value) {
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
                return value;
            }
        };
    }

    public static class FloatTest {
        private final Float floatField;

        FloatTest(final Float floatField) {
            this.floatField = floatField;
        }

        public Float getFloatField() {
            return floatField;
        }
    }
}