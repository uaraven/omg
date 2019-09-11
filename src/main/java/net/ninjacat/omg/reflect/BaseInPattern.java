package net.ninjacat.omg.reflect;

import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.utils.TypeUtils;

import java.util.List;

public abstract class BaseInPattern<T, V> implements PropertyPattern<T> {

    private final Property<T> property;
    private final List<V> matchingValue;

    @SuppressWarnings("unchecked")
    BaseInPattern(final Property<T> property, final List<V> matchingValue) {
        this.property = property;
        this.matchingValue = io.vavr.collection.List.ofAll(matchingValue).map(it -> (V) TypeUtils.convertToBasicType(it)).toJavaList();
    }

    public Property<T> getProperty() {
        return property;
    }

    public List<V> getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final V propertyValue = getPropertyValue(instance);
        if (propertyValue == null || matchingValue.isEmpty()) {
            return false;
        }
        return matchingValue.stream().anyMatch(propertyValue::equals);
    }

    private V getPropertyValue(final T instance) {
        return TypeUtils.getAsType(instance, getProperty(), getType());
    }

    abstract Class<? extends V> getType();

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), "in", matchingValue);
    }
}
