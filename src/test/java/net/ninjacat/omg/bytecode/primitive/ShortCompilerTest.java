package net.ninjacat.omg.bytecode.primitive;

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
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(true));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<ShortTest> pattern = AsmPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.MATCH);

        AsmPatternCompiler.forClass(ShortTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.REGEX);

        AsmPatternCompiler.forClass(ShortTest.class).build(condition);
    }

    private static PropertyCondition<Short> createPropertyCondition(final ConditionMethod method) {
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
                return 42;
            }
        };
    }

    public static class ShortTest {
        private final short shortField;

        ShortTest(final short shortField) {
            this.shortField = shortField;
        }

        public short getShortField() {
            return shortField;
        }
    }
}