package net.ninjacat.omg.bytecode.primitive;

import io.vavr.collection.List;
import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ByteCompilerTest {

    @Test
    public void shouldMatchSimpleEqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.EQ);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(false));
    }

    @Test
    public void shouldMatchSimpleNeqPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.NEQ);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 24)), is(true));
    }


    @Test
    public void shouldMatchSimpleGtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.GT);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 84)), is(true));
    }

    @Test
    public void shouldMatchSimpleLtPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.LT);

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(false));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(true));
    }

    @Test
    public void shouldMatchInPattern() {
        final PropertyCondition<Collection<Byte>> condition =
                new InCondition<>("byteField", List.of((byte) 42, (byte) 84).asJava());

        final PropertyPattern<ByteTest> pattern = AsmPatternCompiler.forClass(ByteTest.class).build(condition);

        assertThat(pattern.matches(new ByteTest((byte) 42)), is(true));
        assertThat(pattern.matches(new ByteTest((byte) 21)), is(false));
    }


    @Test(expected = CompilerException.class)
    public void shouldFailMatchPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.MATCH);

        AsmPatternCompiler.forClass(ByteTest.class).build(condition);
    }

    @Test(expected = CompilerException.class)
    public void shouldFailRegexPattern() {
        final PropertyCondition<Byte> condition = createPropertyCondition(ConditionMethod.REGEX);

        AsmPatternCompiler.forClass(ByteTest.class).build(condition);
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