package net.ninjacat.omg.errors;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Patterns;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestPatternErrors {

    @Theory
    @Test(expected = PatternException.class)
    public void shouldFailWhenNoGetterFound(final CompilerSelectionStrategy strategy) {

        final Condition condition = Conditions.matcher().property("strField").eq("abc").build();

        Patterns.compile(condition, PatternCompiler.forClass(ErrTest.class, strategy));
    }


    @Theory
    @Test(expected = TypeConversionException.class)
    public void shouldFailWhenInvalidConditionType(final CompilerSelectionStrategy strategy) {

        final Condition condition = Conditions.matcher().property("intField").eq("123").build();

        Patterns.compile(condition, PatternCompiler.forClass(ErrTest.class, strategy));
    }

    @SuppressWarnings("WeakerAccess")
    public static class ErrTest {
        private String strField;

        public int getIntField() {
            return 123;
        }
    }
}
