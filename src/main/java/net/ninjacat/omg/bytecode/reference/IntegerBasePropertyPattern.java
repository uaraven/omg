package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Integer properties
 *
 * @param <T>
 */
public abstract class IntegerBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Integer matchingValue;

    protected IntegerBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Integer getMatchingValue() {
        return matchingValue;
    }

    private Integer getMatchingValueConverted(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).intValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Integer.class);
        }
    }
}
