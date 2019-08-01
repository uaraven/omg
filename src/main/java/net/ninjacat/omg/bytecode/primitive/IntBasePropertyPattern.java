package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public abstract class IntBasePropertyPattern<T> extends BasePropertyPattern<T> {


    protected IntBasePropertyPattern(final Property property, final Object matchingValue) {
        super(property, matchingValue);
    }

    protected int getMatchingValueAsInt() {
        final Object mv = getMatchingValue();
        if (mv instanceof Number) {
            return ((Number) mv).intValue();
        } else {
            throw new CompilerException("Cannot convert '%s %s' to 'int'", mv.getClass().getName(), mv);
        }
    }
}
