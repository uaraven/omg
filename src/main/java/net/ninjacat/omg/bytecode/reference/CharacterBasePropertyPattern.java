package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Short properties
 *
 * @param <T>
 */
public abstract class CharacterBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Character matchingValue;

    protected CharacterBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Character getMatchingValue() {
        return matchingValue;
    }

    private Character getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Character) {
            return (Character) mv;
        }
        if (mv instanceof Number) {
            return (char) ((Number) mv).intValue();
        } else
            throw new TypeConversionException(mv.getClass(), mv, Character.class);
    }
}

