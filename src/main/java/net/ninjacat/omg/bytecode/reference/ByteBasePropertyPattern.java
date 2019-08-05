package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Short properties
 *
 * @param <T>
 */
public abstract class ByteBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Byte matchingValue;

    protected ByteBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Byte getMatchingValue() {
        return matchingValue;
    }

    private Byte getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        }
        if (mv instanceof Number) {
            return ((Number) mv).byteValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Byte.class);
        }
    }
}
