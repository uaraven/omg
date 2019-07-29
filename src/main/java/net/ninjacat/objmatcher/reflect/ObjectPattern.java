package net.ninjacat.objmatcher.reflect;

import io.vavr.control.Try;
import net.ninjacat.objmatcher.errors.MatcherException;
import net.ninjacat.objmatcher.patterns.Pattern;
import net.ninjacat.objmatcher.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public class ObjectPattern<T> implements PropertyPattern<T> {

    private final Property property;
    private final Pattern<Object> objectPattern;

    ObjectPattern(final Property property, final Pattern<Object> objectPattern) {
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


}
