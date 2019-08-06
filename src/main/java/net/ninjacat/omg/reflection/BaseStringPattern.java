package net.ninjacat.omg.reflection;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

public abstract class BaseStringPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final String matchingValue;

    BaseStringPattern(final Property property, final String matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public String getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final String propValue = getStringValue(instance);
        return compare(propValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    private String getStringValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> Optional.ofNullable(it).map(Object::toString).orElse(null))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    protected abstract boolean compare(String propertyValue);

    protected abstract String getComparatorAsString();

}
