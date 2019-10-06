package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for boolean properties
 *
 * @param <T>
 */
public abstract class BooleanBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final boolean matchingValue;

    protected BooleanBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public boolean getMatchingValue() {
        return matchingValue;
    }

    private boolean getMatchingValueConverted(final T mv) {
        if (mv instanceof Boolean) {
            return (Boolean) mv;
        } else {
            throw new TypeConversionException(mv.getClass(), mv, boolean.class);
        }
    }
}
