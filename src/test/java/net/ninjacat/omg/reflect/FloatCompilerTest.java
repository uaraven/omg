package net.ninjacat.omg.reflect;

import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.Collection;

import static net.ninjacat.omg.patterns.CompilingStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FloatCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(true));
        assertThat(pattern.matches(new FloatTest(24)), is(false));
    }


    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<Collection<Float>> condition = new InCondition<>(
                "floatField",
                io.vavr.collection.List.of(21.0f, 42.0f, 11.5f).asJava());

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new FloatTest(42.0f)), is(true));
        assertThat(pattern.matches(new FloatTest(21.0f)), is(true));
        assertThat(pattern.matches(new FloatTest(84.0f)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(FloatTest.class, SAFE).build(condition);
    }

    private static PropertyCondition<Float> createPropertyCondition(final ConditionMethod method) {
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
                return 42f;
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