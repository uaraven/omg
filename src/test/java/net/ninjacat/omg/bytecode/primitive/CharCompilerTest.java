package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CharCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<CharTest> pattern = AsmPatternCompiler.forClass(CharTest.class).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharTest((char) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<CharTest> pattern = AsmPatternCompiler.forClass(CharTest.class).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<CharTest> pattern = AsmPatternCompiler.forClass(CharTest.class).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<CharTest> pattern = AsmPatternCompiler.forClass(CharTest.class).build(condition);

        assertThat(pattern.matches(new CharTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharTest((char) 21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.MATCH);

        AsmPatternCompiler.forClass(CharTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.REGEX);

        AsmPatternCompiler.forClass(CharTest.class).build(condition);
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