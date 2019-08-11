package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T>
 */
public abstract class FloatBasePropertyPattern<T> extends BasePropertyPattern<T> {

    private final float matchingValue;

    protected FloatBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueAsFloat(matchingValue);
    }

    public float getMatchingValue() {
        return matchingValue;
    }

    private float getMatchingValueAsFloat(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).floatValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, float.class);
        }
    }

}