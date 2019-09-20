package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T> Type of object containing the property
 */
public abstract class ShortBasePropertyPattern<T> extends BasePropertyPattern<T> {

    private final short matchingValue;

    protected ShortBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public short getMatchingValue() {
        return matchingValue;
    }

    private short getMatchingValueConverted(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).shortValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, short.class);
        }
    }
}
