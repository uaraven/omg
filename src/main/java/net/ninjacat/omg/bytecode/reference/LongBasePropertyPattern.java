package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Long properties
 *
 * @param <T>
 */
public abstract class LongBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Long matchingValue;

    protected LongBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Long getMatchingValue() {
        return matchingValue;
    }

    private Long getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        }
        if (mv instanceof Number) {
            return ((Number) mv).longValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Long.class);
        }
    }
}
