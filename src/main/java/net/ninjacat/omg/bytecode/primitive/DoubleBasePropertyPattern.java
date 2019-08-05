package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T>
 */
public abstract class DoubleBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final double matchingValue;

    protected DoubleBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueAsDouble(matchingValue);
    }

    public double getMatchingValue() {
        return matchingValue;
    }

    private double getMatchingValueAsDouble(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).doubleValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, double.class);
        }
    }

}
