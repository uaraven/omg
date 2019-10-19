package net.ninjacat.omg.reflect;

import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.Collection;

import static net.ninjacat.omg.patterns.CompilingStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class CharCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharTest((char) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 21)), is(true));
    }

    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<Collection<Character>> condition = new InCondition<>(
                "charField",
                io.vavr.collection.List.of((char) 21, (char) 42, (char) 11).asJava());


        final PropertyPattern<CharTest> pattern = ReflectPatternCompiler.forClass(CharTest.class).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharTest((char) 21)), is(true));
        assertThat(pattern.matches(new CharTest((char) 84)), is(false));
    }

    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(CharTest.class, SAFE).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(CharTest.class, SAFE).build(condition);
    }

    private static PropertyCondition<Character> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Character>() {

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
                return "charField";
            }

            @Override
            public Character getValue() {
                return 42;
            }
        };
    }

    public static class CharTest {
        private final char charField;

        CharTest(final char charField) {
            this.charField = charField;
        }

        public char getCharField() {
            return charField;
        }
    }
}