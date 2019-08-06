package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.conditions.RegexCondition;
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
public class StringCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, strategy).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(true));
        assertThat(pattern.matches(new StringTest("wally")), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, strategy).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(false));
        assertThat(pattern.matches(new StringTest("wally")), is(true));
    }


    @Theory
    @Test(expected = OmgException.class)
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.GT);

        PatternCompiler.forClass(StringTest.class, strategy).build(condition);
    }


    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<String>> condition = new InCondition<>(
                "stringField",
                io.vavr.collection.List.of("Waldo", "Wally", "von Ludensdorf").asJava());


        final PropertyPattern<StringTest> pattern = ReflectPatternCompiler.forClass(StringTest.class).build(condition);

        assertThat(pattern.matches(new StringTest("Waldo")), is(true));
        assertThat(pattern.matches(new StringTest("Wally")), is(true));
        assertThat(pattern.matches(new StringTest("Wilma")), is(false));
    }

    @Theory
    public void shouldMatchRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<String> condition = new RegexCondition("stringField", "w.*o");

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, strategy).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(true));
        assertThat(pattern.matches(new StringTest("wally ho")), is(true));
        assertThat(pattern.matches(new StringTest("wilma")), is(false));
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(StringTest.class, strategy).build(condition);
    }

    private static PropertyCondition<String> createPropertyCondition(final ConditionMethod method) {
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
                return "waldo";
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