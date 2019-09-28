package net.ninjacat.omg.reflect;

import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.CompilingStrategy;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ByteCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<ByteTest> pattern = buildPattern(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(true));
    }

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

    @Test(expected = OmgException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.MATCH);

        buildPattern(condition);
    }

    @Test(expected = OmgException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.REGEX);

        buildPattern(condition);
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

    private static PropertyPattern<ByteTest> buildPattern(final PropertyCondition<Byte> condition) {
        return PatternCompiler.forClass(ByteTest.class, CompilingStrategy.SAFE).build(condition);
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