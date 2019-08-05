package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T>
 */
public abstract class IntBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final int matchingValue;

    protected IntBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public int getMatchingValue() {
        return matchingValue;
    }

    private int getMatchingValueConverted(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).intValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, int.class);
        }
    }
}
