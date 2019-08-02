package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T>
 */
public abstract class LongBasePropertyPattern<T> extends BasePropertyPattern<T> {

    protected LongBasePropertyPattern(final Property property, final T matchingValue) {
        super(property, matchingValue);
    }

    protected long getMatchingValueAsLong() {
        final T mv = getMatchingValue();
        if (mv instanceof Number) {
            return ((Number) mv).longValue();
        } else {
            throw new CompilerException("Cannot convert '%s %s' to 'long'", mv.getClass().getName(), mv);
        }
    }

}
