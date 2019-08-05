package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T>
 */
public abstract class CharBasePropertyPattern<T> extends BasePropertyPattern<T> {

    private final char matchingValue;

    protected CharBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public char getMatchingValue() {
        return matchingValue;
    }

    private char getMatchingValueConverted(final T mv) {
        if (mv instanceof Character) {
            return (Character) mv;
        } else if (mv instanceof Number) {
            return (char) ((Number) mv).intValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, char.class);
        }
    }
}
