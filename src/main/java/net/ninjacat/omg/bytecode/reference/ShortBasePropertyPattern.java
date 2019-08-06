package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Short properties
 *
 * @param <T>
 */
public abstract class ShortBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Short matchingValue;

    protected ShortBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Short getMatchingValue() {
        return matchingValue;
    }

    private Short getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Number) {
            return ((Number) mv).shortValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Short.class);
        }
    }
}
