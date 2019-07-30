package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public abstract class IntBasePropertyPattern<T> extends BasePropertyPattern<T> {

    IntBasePropertyPattern(final Property property, final Object matchingValue) {
        super(property, matchingValue);
    }

    private int getMatchingValueAsInt() {
        return Match(getMatchingValue()).of(
                Case($(instanceOf(Long.class)), Long::intValue),
                Case($(instanceOf(Short.class)), Short::intValue),
                Case($(instanceOf(Byte.class)), Byte::intValue),
                Case($(instanceOf(Character.class)), chr -> (int) chr),
                Case($(), () -> {
                    throw new CompilerException("Cannot convert '%s' to 'int'", getMatchingValue());
                })
        );
    }
}
