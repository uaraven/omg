package net.ninjacat.objmatcher.matcher;

import java.util.function.Predicate;

/**
 * Compares object to a pattern
 *
 * @param <T> Type of the object to compare to
 */
@FunctionalInterface
public interface ObjectMatcher<T> extends Predicate<T> {
    default boolean matches(final T object) {
        return test(object);
    }
}
