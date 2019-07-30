package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.patterns.PropertyPattern;

/**
 * Base class for matching property value.
 * @param <T>
 */
public abstract class BasePropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Object matchingValue;

    public BasePropertyPattern(final Property property, final Object matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public Property getProperty() {
        return property;
    }

    public Object getMatchingValue() {
        return matchingValue;
    }

    @Override
    public abstract boolean matches(final T instance);

}
