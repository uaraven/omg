package net.ninjacat.omg.compilation;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
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
public class ByteCompilerTest {

    @Theory
    public void shouldMatchSimpleEqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition, strategy);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(false));
    }

    @Theory
    public void shouldMatchSimpleNeqPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition, strategy);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(true));
    }


    @Theory
    public void shouldMatchSimpleGtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition, strategy);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 84)), is(true));
    }

    @Theory
    public void shouldMatchSimpleLtPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition, strategy);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(true));
    }

    // TODO: Convert to Theory to test both reflection and compiled pattern
    @Test
    public void shouldMatchSimpleInPattern() {
        final PropertyCondition<List<Byte>> condition = new InCondition<>(
                "byteField",
                io.vavr.collection.List.of((byte) 21, (byte) 42, (byte) 11).asJava());


        final PropertyPattern<ByteTest> pattern = ReflectPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 84)), is(false));
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.MATCH);

        buildPattern(condition, strategy);
    }

    @Theory
    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern(final CompilerSelectionStrategy strategy) {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.REGEX);

        buildPattern(condition, strategy);
    }

    private static PropertyCondition<Byte> createPropertyCondition(final ConditionMethod method) {
        return new PropertyCondition<Byte>() {

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
                return "byteField";
            }

            @Override
            public Byte getValue() {
                return 42;
            }
        };
    }

    private static PropertyPattern<ByteTest> buildPattern(final PropertyCondition<Byte> condition, final CompilerSelectionStrategy strategy) {
        return PatternCompiler.forClass(ByteTest.class, strategy).build(condition);
    }

    public static class ByteTest {

        private final byte byteField;

        ByteTest(final byte byteField) {
            this.byteField = byteField;
        }

        public byte getByteField() {
            return byteField;
        }
    }
}