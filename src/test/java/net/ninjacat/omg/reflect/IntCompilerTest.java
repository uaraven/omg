package net.ninjacat.omg.reflect;

import lombok.Value;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class IntCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(24)), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(true));
        assertThat(pattern.matches(new IntTest(84)), is(false));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<List<Integer>> condition = new InCondition<>(
                "intField",
                io.vavr.collection.List.of(21, 42, 11).asJava());


        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(true));
        assertThat(pattern.matches(new IntTest(84)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.MATCH);


        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, strategy).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(true));
        assertThat(pattern.matches(new IntTest(84)), is(false));
    }

    @Theory
    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(IntTest.class, strategy).build(condition);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(IntTest.class, strategy).build(condition);
    }

    private static PropertyCondition<Integer> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Integer>() {

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
                return "intField";
            }

            @Override
            public Integer getValue() {
                return 42;
            }
        };
    }

    @Value
    public static class IntTest {
        int intField;
    }
}