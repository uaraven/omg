package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

public abstract class BaseObjectPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Object matchingValue;

    BaseObjectPattern(final Property property, final Object matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getOperator(), matchingValue);
    }

    protected abstract String getOperator();

    public Property getProperty() {
        return property;
    }

    public Object getMatchingValue() {
        return matchingValue;
    }

    Object getPropertyValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> Optional.ofNullable(it).orElse(null))
                .getOrElseThrow(err -> new CompilerException(err, "Failed to match property %s in %s", property, instance));
    }
}
