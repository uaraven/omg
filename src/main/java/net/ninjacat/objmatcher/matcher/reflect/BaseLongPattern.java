package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.control.Try;
import net.ninjacat.objmatcher.matcher.errors.MatcherException;
import net.ninjacat.objmatcher.matcher.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public abstract class BaseLongPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final long matchingValue;

    BaseLongPattern(final Property property, final long matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    private long getLongValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> (Long) TypeUtils.convertToBasicType(it))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    @Override
    public boolean matches(final T instance) {
        final long propValue = getLongValue(instance);
        return compare(propValue, matchingValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    protected abstract boolean compare(long propertyValue, long matchingValue);

    protected abstract String getComparatorAsString();

}
