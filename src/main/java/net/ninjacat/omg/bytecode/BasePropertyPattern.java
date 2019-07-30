package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.reflect.Property;

/**
 * Base class for matching property value.
 * @param <T>
 * @param <P>
 */
public abstract class BasePropertyPattern<T, P> implements PropertyPattern<T> {
    private final Property property;
    private final P matchingValue;

    public BasePropertyPattern(final Property property, final P matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public Property getProperty() {
        return property;
    }

    public P getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final P propertyValue = getPropertyValue(instance);
        if (propertyValue == null) {
            return matchingValue == null;
        }
        if (matchingValue == null) {
            return false;
        }
        return matchingValue.equals(propertyValue);
    }

    protected abstract P getPropertyValue(T instance);
}
