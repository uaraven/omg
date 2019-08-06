package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Enum properties
 *
 * @param <T>
 */
public abstract class EnumBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Enum matchingValue;

    protected EnumBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Enum getMatchingValue() {
        return matchingValue;
    }

    private Enum getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Enum) {
            return (Enum) mv;
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Enum.class);
        }
    }
}
