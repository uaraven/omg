package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T> Type of object containing the property
 */
public abstract class LongBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final long matchingValue;

    protected LongBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueAsLong(matchingValue);
    }

    public long getMatchingValue() {
        return matchingValue;
    }

    private long getMatchingValueAsLong(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).longValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, long.class);
        }
    }

}
