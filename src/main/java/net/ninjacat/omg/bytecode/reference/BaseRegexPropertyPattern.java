package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.util.regex.Pattern;

/**
 * Base class for regex matching property value.
 * @param <T>
 */
public abstract class BaseRegexPropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Pattern regex;

    public BaseRegexPropertyPattern(final Property property, final Object matchingValue) {
        this.property = property;
        if (matchingValue instanceof String) {
            this.regex = Pattern.compile((String) matchingValue);
        } else {
            throw new CompilerException("Value must be string for REGEX condition, got '%s' instead", matchingValue);
        }
    }

    public Property getProperty() {
        return property;
    }

    public Pattern getMatchingValue() {
        return regex;
    }

    @Override
    public abstract boolean matches(final T instance);

}
