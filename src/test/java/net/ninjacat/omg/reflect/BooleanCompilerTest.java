package net.ninjacat.omg.reflect;

import io.vavr.collection.List;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BooleanCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<BooleanTest> pattern = ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);

        assertThat(pattern.matches(new BooleanTest(true)), is(true));
        assertThat(pattern.matches(new BooleanTest(false)), is(false));
    }


    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<BooleanTest> pattern = ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);

        assertThat(pattern.matches(new BooleanTest(true)), is(false));
        assertThat(pattern.matches(new BooleanTest(false)), is(true));
    }


    @Test(expected = CompilerException.class)
    public void shouldFailGtPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.GT);
        ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailLtPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.LT);
        ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailInPattern() {
        final PropertyCondition<java.util.List<Boolean>> condition =
                new InCondition<>("boolField", List.of(true, false).asJava());

        ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.MATCH);

        ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Boolean> condition = createPropertyCondition(ConditionMethod.REGEX);

        ReflectPatternCompiler.forClass(BooleanTest.class).build(condition);
    }

    private static PropertyCondition<Boolean> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Boolean>() {

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
                return "boolField";
            }

            @Override
            public Boolean getValue() {
                return true;
            }
        };
    }

    public static class BooleanTest {
        private final boolean boolField;

        BooleanTest(final boolean boolField) {
            this.boolField = boolField;
        }

        public boolean isBoolField() {
            return boolField;
        }
    }
}