package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Double properties
 *
 * @param <T>
 */
public abstract class DoubleBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Double matchingValue;

    protected DoubleBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Double getMatchingValue() {
        return matchingValue;
    }

    private Double getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        }
        if (mv instanceof Number) {
            return ((Number) mv).doubleValue();
        } else
            throw new TypeConversionException(mv.getClass(), mv, Double.class);
    }
}

