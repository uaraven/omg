package net.ninjacat.omg.compilation;

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
public class DoubleCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<DoubleTest> pattern = PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(true));
        assertThat(pattern.matches(new DoubleTest(24)), is(false));
    }


    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<DoubleTest> pattern = PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<DoubleTest> pattern = PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<DoubleTest> pattern = PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Double>> condition = new InCondition<>(
                "doubleField",
                io.vavr.collection.List.of(21.0, 42.0, 11.5).asJava());


        final PropertyPattern<DoubleTest> pattern = ReflectPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42.0)), is(true));
        assertThat(pattern.matches(new DoubleTest(21.0)), is(true));
        assertThat(pattern.matches(new DoubleTest(84.0)), is(false));
    }

    @Theory
    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.MATCH);
        PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(DoubleTest.class, strategy).build(condition);
    }

    private static PropertyCondition<Double> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Double>() {

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
                return "doubleField";
            }

            @Override
            public Double getValue() {
                return (double) 42;
            }
        };
    }

    public static class DoubleTest {
        private final double doubleField;

        DoubleTest(final double doubleField) {
            this.doubleField = doubleField;
        }

        public double getDoubleField() {
            return doubleField;
        }
    }
}