package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T>
 */
public abstract class CharBasePropertyPattern<T> extends BasePropertyPattern<T> {


    protected CharBasePropertyPattern(final Property property, final T matchingValue) {
        super(property, matchingValue);
    }

    protected char getMatchingValueConverted() {
        final T mv = getMatchingValue();
        if (mv instanceof Character) {
            return (Character) mv;
        } else if (mv instanceof Number) {
            return (char) ((Number) mv).intValue();
        } else {
            throw new CompilerException("Cannot convert '%s %s' to 'int'", mv.getClass().getName(), mv);
        }
    }
}
