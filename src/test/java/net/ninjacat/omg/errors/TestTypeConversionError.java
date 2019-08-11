package net.ninjacat.omg.errors;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Patterns;
import org.immutables.value.Value;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestTypeConversionError {

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailIntTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("intField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailByteTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("byteField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailTypeCharConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("charField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("doubleField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailFloatTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("floatField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailLongTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("longField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailShortTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("shortField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailStringTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("stringField").eq(1).build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }


    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedIntTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedIntField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedByteTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedByteField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailTypeBoxedCharConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedCharField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedDoubleTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedDoubleField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedLongTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedLongField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedShortTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedShortField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailBoxedFloatTypeConversion(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher().property("boxedFloatField").eq("1").build();

        Patterns.compile(condition, PatternCompiler.forClass(FieldTest.class, strategy));
    }

    @Value.Immutable
    public interface FieldTest {
        int getIntField();

        long getLongField();

        short getShortField();

        byte getByteField();

        char getCharField();

        double getDoubleField();

        float getFloatField();

        String getStringField();

        Integer getBoxedIntField();

        Long getBoxedLongField();

        Short getBoxedShortField();

        Byte getBoxedByteField();

        Character getBoxedCharField();

        Double getBoxedDoubleField();

        Float getBoxedFloatField();
    }

}
