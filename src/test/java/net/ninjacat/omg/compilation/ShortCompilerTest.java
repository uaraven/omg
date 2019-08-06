package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
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
public class ShortCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<ShortTest> pattern = PatternCompiler.forClass(ShortTest.class, strategy).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(true));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<ShortTest> pattern = PatternCompiler.forClass(ShortTest.class, strategy).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<ShortTest> pattern = PatternCompiler.forClass(ShortTest.class, strategy).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<ShortTest> pattern = PatternCompiler.forClass(ShortTest.class, strategy).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(false));
        assertThat(pattern.matches(new ShortTest((short) 21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Short>> condition = new InCondition<>(
                "shortField",
                io.vavr.collection.List.of((short) 21, (short) 42, (short) 11).asJava());


        final PropertyPattern<ShortTest> pattern = ReflectPatternCompiler.forClass(ShortTest.class).build(condition);

        assertThat(pattern.matches(new ShortTest((short) 42)), is(true));
        assertThat(pattern.matches(new ShortTest((short) 21)), is(true));
        assertThat(pattern.matches(new ShortTest((short) 84)), is(false));
    }

    @Theory
    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(ShortTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Short> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(ShortTest.class, strategy).build(condition);
    }

    private static PropertyCondition<Short> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Short>() {

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
                return "shortField";
            }

            @Override
            public Short getValue() {
                return 42;
            }
        };
    }

    public static class ShortTest {
        private final short shortField;

        ShortTest(final short shortField) {
            this.shortField = shortField;
        }

        public short getShortField() {
            return shortField;
        }
    }
}