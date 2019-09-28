package net.ninjacat.omg.reflect;

import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.List;

import static net.ninjacat.omg.patterns.CompilingStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnumCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<EnumTest> pattern = PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<EnumTest> pattern = PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(false));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(true));
    }


    @Test(expected = OmgException.class)
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.GT);

        PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);
    }

    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<EnumValues>> condition = new InCondition<>(
                "enumField",
                io.vavr.collection.List.of(EnumValues.E1, EnumValues.E2, EnumValues.E4).asJava());


        final PropertyPattern<EnumTest> pattern = PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E3)), is(false));
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(EnumTest.class, SAFE).build(condition);
    }

    private static PropertyCondition<EnumValues> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<EnumValues>() {

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
                return "enumField";
            }

            @Override
            public EnumValues getValue() {
                return EnumValues.E1;
            }
        };
    }

    public enum EnumValues {
        E1,
        E2,
        E3,
        E4
    }

    public static class EnumTest {
        private final EnumValues enumField;

        EnumTest(final EnumValues enumField) {
            this.enumField = enumField;
        }

        public EnumValues getEnumField() {
            return enumField;
        }
    }
}