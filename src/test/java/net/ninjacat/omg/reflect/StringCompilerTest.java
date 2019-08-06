package net.ninjacat.omg.reflect;

import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.conditions.RegexCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.List;

import static net.ninjacat.omg.CompilerSelectionStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(true));
        assertThat(pattern.matches(new StringTest("wally")), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(false));
        assertThat(pattern.matches(new StringTest("wally")), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final InCondition<String> condition = new InCondition<>("stringField", io.vavr.collection.List.of("wally", "waldo").asJava());

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(true));
        assertThat(pattern.matches(new StringTest("wilma")), is(false));
    }

    @Test(expected = OmgException.class)
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.GT);

        PatternCompiler.forClass(StringTest.class, SAFE).build(condition);
    }

    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<String>> condition = new InCondition<>(
                "stringField",
                io.vavr.collection.List.of("Waldo", "Wally", "von Ludensdorf").asJava());


        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new StringTest("Waldo")), is(true));
        assertThat(pattern.matches(new StringTest("Wally")), is(true));
        assertThat(pattern.matches(new StringTest("Wilma")), is(false));
    }

    @Test
    public void shouldMatchRegexPattern() {
        final PropertyCondition<String> condition = new RegexCondition("stringField", "w.*o");

        final PropertyPattern<StringTest> pattern = PatternCompiler.forClass(StringTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new StringTest("waldo")), is(true));
        assertThat(pattern.matches(new StringTest("wally ho")), is(true));
        assertThat(pattern.matches(new StringTest("wilma")), is(false));
    }

    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(StringTest.class, SAFE).build(condition);
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