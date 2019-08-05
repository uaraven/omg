package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Short properties
 *
 * @param <T>
 */
public abstract class StringBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final String matchingValue;

    protected StringBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public String getMatchingValue() {
        return matchingValue;
    }

    private String getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        }
        if (mv instanceof String) {
            return (String) mv;
        } else {
            throw new TypeConversionException(mv.getClass(), mv, String.class);
        }
    }
}
