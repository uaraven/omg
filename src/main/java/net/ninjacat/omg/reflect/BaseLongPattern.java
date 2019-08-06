package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public abstract class BaseLongPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final long matchingValue;

    BaseLongPattern(final Property property, final long matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public long getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final long propValue = getLongValue(instance);
        return compare(propValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    private long getLongValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> (Long) TypeUtils.convertToBasicType(it))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    protected abstract boolean compare(long propertyValue);

    protected abstract String getComparatorAsString();

}
