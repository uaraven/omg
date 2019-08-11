package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T>
 */
public abstract class ByteBasePropertyPattern<T> extends BasePropertyPattern<T> {

    private final byte matchingValue;

    protected ByteBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public byte getMatchingValue() {
        return matchingValue;
    }

    private byte getMatchingValueConverted(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).byteValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, byte.class);
        }
    }
}