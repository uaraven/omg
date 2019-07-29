package net.ninjacat.objmatcher.patterns;

import java.util.function.Predicate;

/**
 * Pattern is a condition applied to specific class.
 */
@FunctionalInterface
public interface Pattern<T> extends Predicate<T> {
    boolean matches(final T instance);

    default boolean test(final T instance) {
        return matches(instance);
    }
}
