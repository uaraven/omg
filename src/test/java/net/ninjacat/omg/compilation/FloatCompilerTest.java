package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.reflect.ReflectPatternCompiler;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class FloatCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, strategy).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(true));
        assertThat(pattern.matches(new FloatTest(24)), is(false));
    }


    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, strategy).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, strategy).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<FloatTest> pattern = PatternCompiler.forClass(FloatTest.class, strategy).build(condition);

        assertThat(pattern.matches(new FloatTest(42)), is(false));
        assertThat(pattern.matches(new FloatTest(21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Float>> condition = new InCondition<>(
                "floatField",
                io.vavr.collection.List.of(21.0f, 42.0f, 11.5f).asJava());


        final PropertyPattern<FloatTest> pattern = ReflectPatternCompiler.forClass(FloatTest.class).build(condition);

        assertThat(pattern.matches(new FloatTest(42.0f)), is(true));
        assertThat(pattern.matches(new FloatTest(21.0f)), is(true));
        assertThat(pattern.matches(new FloatTest(84.0f)), is(false));
    }

    @Theory
    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(FloatTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Float> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(FloatTest.class, strategy).build(condition);
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