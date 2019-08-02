package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for int properties
 *
 * @param <T>
 */
public abstract class ByteBasePropertyPattern<T> extends BasePropertyPattern<T> {


    protected ByteBasePropertyPattern(final Property property, final T matchingValue) {
        super(property, matchingValue);
    }

    protected byte getMatchingValueConverted() {
        final T mv = getMatchingValue();
        if (mv instanceof Number) {
            return ((Number) mv).byteValue();
        } else {
            throw new CompilerException("Cannot convert '%s %s' to 'int'", mv.getClass().getName(), mv);
        }
    }
}
