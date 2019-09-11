package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.utils.TypeUtils;

import java.lang.invoke.MethodHandle;

public abstract class BaseDoublePattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final double matchingValue;

    BaseDoublePattern(final Property property, final double matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public double getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final double propValue = getDoubleValue(instance);
        return compare(propValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    private double getDoubleValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> (Double) TypeUtils.convertToBasicType(it))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    protected abstract boolean compare(double propertyValue);

    protected abstract String getComparatorAsString();

}
