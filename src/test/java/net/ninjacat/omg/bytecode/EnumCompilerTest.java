package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnumCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<TestE> condition = createPropertyCondition(ConditionMethod.EQ, TestE.E1);

        final PropertyPattern<EnumTest> pattern = AsmPatternCompiler.forClass(EnumTest.class).build(condition);

        assertThat(pattern.matches(new EnumTest(TestE.E1)), is(true));
        assertThat(pattern.matches(new EnumTest(TestE.E2)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<TestE> condition = createPropertyCondition(ConditionMethod.NEQ, TestE.E1);

        final PropertyPattern<EnumTest> pattern = AsmPatternCompiler.forClass(EnumTest.class).build(condition);

        assertThat(pattern.matches(new EnumTest(TestE.E1)), is(false));
        assertThat(pattern.matches(new EnumTest(TestE.E2)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailOnGtPattern() {
        final PropertyCondition<TestE> condition = createPropertyCondition(ConditionMethod.GT, TestE.E1);

        AsmPatternCompiler.forClass(EnumTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailOnLtPattern() {
        final PropertyCondition<TestE> condition = createPropertyCondition(ConditionMethod.LT, TestE.E1);

        AsmPatternCompiler.forClass(EnumTest.class).build(condition);
    }


    private static PropertyCondition<TestE> createPropertyCondition(final ConditionMethod method, final TestE value) {
        return new PropertyCondition<TestE>() {

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
                return "eField";
            }

            @Override
            public TestE getValue() {
                return value;
            }
        };
    }

    public enum TestE {
        E1,
        E2;
    }

    public static class EnumTest {
        private final TestE eField;

        EnumTest(final TestE eField) {
            this.eField = eField;
        }

        public TestE getEField() {
            return eField;
        }
    }
}