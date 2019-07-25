package net.ninjacat.objmatcher.matcher;

import java.util.function.Predicate;

@FunctionalInterface
public interface ObjectMatcher<T> extends Predicate<T> {
    default boolean matches(final T object) {
        return test(object);
    }
}
