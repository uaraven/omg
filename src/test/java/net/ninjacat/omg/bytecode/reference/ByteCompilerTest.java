package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ByteCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.EQ, (byte) 42);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.NEQ, (byte) 42);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.GT, (byte) 42);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.LT, (byte) 42);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(true));
    }

    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.MATCH, (byte) 42);

        AsmPatternCompiler.forClass(ByteTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.REGEX, (byte) 42);

        AsmPatternCompiler.forClass(ByteTest.class).build(condition);
    }

    private static PropertyCondition<Byte> createPropertyCondition(final ConditionMethod method, final Byte value) {
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
                return value;
            }
        };
    }

    public static class ByteTest {
        private final Byte byteField;

        ByteTest(final Byte ByteField) {
            this.byteField = ByteField;
        }

        public Byte getByteField() {
            return byteField;
        }
    }
}