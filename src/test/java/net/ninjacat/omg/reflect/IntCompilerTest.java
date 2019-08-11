package net.ninjacat.omg.reflect;

import lombok.Value;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.util.List;

import static net.ninjacat.omg.CompilerSelectionStrategy.SAFE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class IntCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(false));
        assertThat(pattern.matches(new IntTest(21)), is(true));
        assertThat(pattern.matches(new IntTest(84)), is(false));
    }

    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Integer>> condition = new InCondition<>(
                "intField",
                io.vavr.collection.List.of(21, 42, 11).asJava());


        final PropertyPattern<IntTest> pattern = PatternCompiler.forClass(IntTest.class, SAFE).build(condition);

        assertThat(pattern.matches(new IntTest(42)), is(true));
        assertThat(pattern.matches(new IntTest(21)), is(true));
        assertThat(pattern.matches(new IntTest(84)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.MATCH);

        PatternCompiler.forClass(IntTest.class, SAFE).build(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Integer> condition = createPropertyCondition(ConditionMethod.REGEX);

        PatternCompiler.forClass(IntTest.class, SAFE).build(condition);
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