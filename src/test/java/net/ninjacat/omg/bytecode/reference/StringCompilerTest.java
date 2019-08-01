package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.EQ, "jabba");

        final PropertyPattern<StringTest> pattern = AsmPatternCompiler.forClass(StringTest.class).build(condition);

        assertThat(pattern.matches(new StringTest("jabba")), is(true));
        assertThat(pattern.matches(new StringTest("bubba")), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.NEQ, "jabba");

        final PropertyPattern<StringTest> pattern = AsmPatternCompiler.forClass(StringTest.class).build(condition);

        assertThat(pattern.matches(new StringTest("jabba")), is(false));
        assertThat(pattern.matches(new StringTest("bubba")), is(true));
    }

    @Test
    public void shouldMatchRegexPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.REGEX, "Hell.*!");

        final PropertyPattern<StringTest> pattern = AsmPatternCompiler.forClass(StringTest.class).build(condition);

        assertThat(pattern.matches(new StringTest("Hello, world!")), is(true));
        assertThat(pattern.matches(new StringTest("Hello, boredom :(")), is(false));
    }


    @Test(expected = CompilerException.class)
    public void shouldFailOnGtPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.GT, "gt");

        AsmPatternCompiler.forClass(StringTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailOnLtPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.LT, "lt");

        AsmPatternCompiler.forClass(StringTest.class).build(condition);
    }


    private static PropertyCondition<String> createPropertyCondition(final ConditionMethod method, final String value) {
        return new PropertyCondition<String>() {

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
                return "stringField";
            }

            @Override
            public String getValue() {
                return value;
            }
        };
    }

    public static class StringTest {
        private final String stringField;

        StringTest(final String stringField) {
            this.stringField = stringField;
        }

        public String getStringField() {
            return stringField;
        }
    }
}