package net.ninjacat.omg.bytecode.reference;

import io.vavr.collection.List;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.immutables.value.Value;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LongCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.EQ, 42L);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(getTestInstance(42L)), is(true));
        assertThat(pattern.matches(getTestInstance(24L)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.NEQ, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(getTestInstance(42L)), is(false));
        assertThat(pattern.matches(getTestInstance(24L)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.GT, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(getTestInstance(42L)), is(false));
        assertThat(pattern.matches(getTestInstance(84L)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.LT, 42);

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(getTestInstance(42L)), is(false));
        assertThat(pattern.matches(getTestInstance(21L)), is(true));
    }


    @Test
    public void shouldMatchInPattern() {
        final InCondition<Long> condition = new InCondition<>("longField", List.of(42L, 43L).asJava());

        final PropertyPattern<LongTest> pattern = AsmPatternCompiler.forClass(LongTest.class).build(condition);

        assertThat(pattern.matches(getTestInstance(42L)), is(true));
        assertThat(pattern.matches(getTestInstance(21L)), is(false));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.MATCH, 42);

        AsmPatternCompiler.forClass(LongTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Long> condition = createPropertyCondition(ConditionMethod.REGEX, 42);

        AsmPatternCompiler.forClass(LongTest.class).build(condition);
    }

    private static PropertyCondition<Long> createPropertyCondition(final ConditionMethod method, final long value) {
        return new PropertyCondition<Long>() {

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
                return "longField";
            }

            @Override
            public Long getValue() {
                return value;
            }
        };
    }

    private static LongTest getTestInstance(final long l) {
        return ImmutableLongTest.of(l);
    }

    @Value.Immutable
    @Value.Style(allParameters = true)
    public interface LongTest {

        Long getLongField();
    }
}