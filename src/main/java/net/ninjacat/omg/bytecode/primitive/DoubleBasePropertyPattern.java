package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T>
 */
public abstract class DoubleBasePropertyPattern<T> extends BasePropertyPattern<T> {

    protected DoubleBasePropertyPattern(final Property property, final T matchingValue) {
        super(property, matchingValue);
    }

    protected double getMatchingValueAsDouble() {
        final T mv = getMatchingValue();
        if (mv instanceof Number) {
            return ((Number) mv).doubleValue();
        } else {
            throw new CompilerException("Cannot convert '%s %s' to 'double'", mv.getClass().getName(), mv);
        }
    }

}
