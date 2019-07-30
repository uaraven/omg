package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public class ObjectPattern<T> implements PropertyPattern<T> {

    private final Property property;
    private final Pattern objectPattern;

    ObjectPattern(final Property property, final Pattern objectPattern) {
        this.property = property;
        this.objectPattern = objectPattern;
    }

    @Override
    public boolean matches(final T instance) {
        final Object propValue = getObjectValue(instance);
        return objectPattern.matches(propValue);
    }

    private Object getObjectValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    @Override
    public String toString() {
        return String.format("'%s' %% '%s'", property.toString(), objectPattern);
    }
}
