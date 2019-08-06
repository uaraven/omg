package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.reflection.ReflectPatternCompiler;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class EnumCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<EnumTest> pattern = PatternCompiler.forClass(EnumTest.class, strategy).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<EnumTest> pattern = PatternCompiler.forClass(EnumTest.class, strategy).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(false));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(true));
    }


    @Theory
    @Test(expected = OmgException.class)
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.GT);

        PatternCompiler.forClass(EnumTest.class, strategy).build(condition);
    }


    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<EnumValues>> condition = new InCondition<>(
                "enumField",
                io.vavr.collection.List.of(EnumValues.E1, EnumValues.E2, EnumValues.E4).asJava());


        final PropertyPattern<EnumTest> pattern = ReflectPatternCompiler.forClass(EnumTest.class).build(condition);

        assertThat(pattern.matches(new EnumTest(EnumValues.E1)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E2)), is(true));
        assertThat(pattern.matches(new EnumTest(EnumValues.E3)), is(false));
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(EnumTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<EnumValues> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(EnumTest.class, strategy).build(condition);
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