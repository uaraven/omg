package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.reflect.ReflectPatternCompiler;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class CharCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, strategy).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharTest((char) 24)), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, strategy).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, strategy).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<CharTest> pattern = PatternCompiler.forClass(CharTest.class, strategy).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Character>> condition = new InCondition<>(
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

        PatternCompiler.forClass(CharTest.class, CompilerSelectionStrategy.FAST).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(CharTest.class, CompilerSelectionStrategy.SAFE).build(condition);
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