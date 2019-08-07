package net.ninjacat.omg.bytecode.reference;

import io.vavr.collection.List;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CharacterCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.EQ, (char) 42);

        final PropertyPattern<CharacterTest> pattern = AsmPatternCompiler.forClass(CharacterTest.class).build(condition);

        assertThat(pattern.matches(new CharacterTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharacterTest((char) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.NEQ, (char) 42);

        final PropertyPattern<CharacterTest> pattern = AsmPatternCompiler.forClass(CharacterTest.class).build(condition);

        assertThat(pattern.matches(new CharacterTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharacterTest((char) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.GT, (char) 42);

        final PropertyPattern<CharacterTest> pattern = AsmPatternCompiler.forClass(CharacterTest.class).build(condition);

        assertThat(pattern.matches(new CharacterTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharacterTest((char) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.LT, (char) 42);

        final PropertyPattern<CharacterTest> pattern = AsmPatternCompiler.forClass(CharacterTest.class).build(condition);

        assertThat(pattern.matches(new CharacterTest((char) 42)), is(false));
        assertThat(pattern.matches(new CharacterTest((char) 21)), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final InCondition<Character> condition = new InCondition<>("characterField", List.of((char)42, (char)43).asJava());

        final PropertyPattern<CharacterTest> pattern = AsmPatternCompiler.forClass(CharacterTest.class).build(condition);

        assertThat(pattern.matches(new CharacterTest((char) 42)), is(true));
        assertThat(pattern.matches(new CharacterTest((char) 21)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.MATCH, (char) 42);

        AsmPatternCompiler.forClass(CharacterTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Character> condition = createPropertyCondition(ConditionMethod.REGEX, (char) 42);

        AsmPatternCompiler.forClass(CharacterTest.class).build(condition);
    }

    private static PropertyCondition<Character> createPropertyCondition(final ConditionMethod method, final Character value) {
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
                return "characterField";
            }

            @Override
            public Character getValue() {
                return value;
            }
        };
    }

    public static class CharacterTest {
        private final Character characterField;

        CharacterTest(final Character CharacterField) {
            this.characterField = CharacterField;
        }

        public Character getCharacterField() {
            return characterField;
        }
    }
}