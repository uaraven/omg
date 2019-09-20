package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.patterns.PropertyPattern;

/**
 * Base class for matching property value.
 * @param <T> Type of object containing the property
 */
public abstract class BasePropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;

    public BasePropertyPattern(final Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    @Override
    public abstract boolean matches(final T instance);

}
