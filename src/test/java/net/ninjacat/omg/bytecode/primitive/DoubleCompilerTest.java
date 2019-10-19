package net.ninjacat.omg.bytecode.primitive;

import io.vavr.collection.List;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DoubleCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.EQ, 42);

        final PropertyPattern<DoubleTest> pattern = AsmPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(true));
        assertThat(pattern.matches(new DoubleTest(24)), is(false));
    }


    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<DoubleTest> pattern = AsmPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.GT, 42);

        final PropertyPattern<DoubleTest> pattern = AsmPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.LT, 42);

        final PropertyPattern<DoubleTest> pattern = AsmPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42)), is(false));
        assertThat(pattern.matches(new DoubleTest(21)), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final PropertyCondition<Collection<Double>> condition = new InCondition<>("doubleField", List.of(42.0, 84.0).asJava());

        final PropertyPattern<DoubleTest> pattern = AsmPatternCompiler.forClass(DoubleTest.class).build(condition);

        assertThat(pattern.matches(new DoubleTest(42.0)), is(true));
        assertThat(pattern.matches(new DoubleTest(21.0)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.MATCH, 42);

        AsmPatternCompiler.forClass(DoubleTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Double> condition = createPropertyCondition(ConditionMethod.REGEX, 42);

        AsmPatternCompiler.forClass(DoubleTest.class).build(condition);
    }

    private static PropertyCondition<Double> createPropertyCondition(final ConditionMethod method, final double value) {
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
                return value;
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