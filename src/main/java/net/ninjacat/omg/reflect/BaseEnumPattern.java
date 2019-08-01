package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public abstract class BaseEnumPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final String matchingValue;

    BaseEnumPattern(final Property property, final String matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public String getMatchingValue() {
        return matchingValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(final T instance) {
        final Enum propValue = getEnumValue(instance);
        final Enum matchingEnumValue = Enum.valueOf(property.getType(), matchingValue);
        return compare(propValue, matchingEnumValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    private Enum getEnumValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> (Enum)it)
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property '%s' in '%s'", property, instance));
    }

    protected abstract boolean compare(Enum propertyValue, Enum matchingValue);

    protected abstract String getComparatorAsString();

}
