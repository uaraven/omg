package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ShortCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.EQ, (short) 42);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(true));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.NEQ, (short) 42);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.GT, (short) 42);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.LT, (short) 42);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.MATCH, (short) 42);

        AsmPatternCompiler.forClass(ShortTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.REGEX, (short) 42);

        AsmPatternCompiler.forClass(ShortTest.class).build(condition);
    }

    private static PropertyCondition<Short> createPropertyCondition(final ConditionMethod method, final Short value) {
        return new PropertyCondition<Short>() {

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
                return "shortField";
            }

            @Override
            public Short getValue() {
                return value;
            }
        };
    }

    public static class ShortTest {
        private final Short shortField;

        ShortTest(final Short ShortField) {
            this.shortField = ShortField;
        }

        public Short getShortField() {
            return shortField;
        }
    }
}