package net.ninjacat.omg.bytecode.reference;

import io.vavr.collection.HashSet;
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

public class IntegerCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.EQ, 42);

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.GT, 42);

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.LT, 42);

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(21)), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final InCondition<Integer> condition = new InCondition<>("intField", List.of(42, 43).asJava());

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(false));
    }


    @Test
    public void shouldMatchInPatternWithSet() {
        final InCondition<Integer> condition = new InCondition<>("intField", HashSet.of(42, 43).toJavaSet());

        final PropertyPattern<IntTest> pattern = AsmPatternCompiler.forClass(IntTest.class).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.MATCH, 42);

        AsmPatternCompiler.forClass(IntTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.REGEX, 42);

        AsmPatternCompiler.forClass(IntTest.class).build(condition);
    }

    private static PropertyCondition<Integer> createPropertyCondition(final ConditionMethod method, final int value) {
        return new PropertyCondition<Integer>() {

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
                return "intField";
            }

            @Override
            public Integer getValue() {
                return value;
            }
        };
    }

    public static class IntTest {
        private final Integer intField;

        IntTest(final int intField) {
            this.intField = intField;
        }

        public Integer getIntField() {
            return intField;
        }
    }
}