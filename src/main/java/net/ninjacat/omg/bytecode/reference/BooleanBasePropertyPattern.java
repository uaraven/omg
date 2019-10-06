package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Boolean properties
 *
 * @param <T>
 */
public abstract class BooleanBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Boolean matchingValue;

    protected BooleanBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Boolean getMatchingValue() {
        return matchingValue;
    }

    private Boolean getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Boolean) {
            return ((Boolean) mv);
        } else
            throw new TypeConversionException(mv.getClass(), mv, Boolean.class);
    }
}

