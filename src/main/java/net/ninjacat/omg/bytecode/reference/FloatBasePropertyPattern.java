package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Double properties
 *
 * @param <T>
 */
public abstract class FloatBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Float matchingValue;

    protected FloatBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Float getMatchingValue() {
        return matchingValue;
    }

    private Float getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Number) {
            return ((Number) mv).floatValue();
        } else
            throw new TypeConversionException(mv.getClass(), mv, Float.class);
    }
}

