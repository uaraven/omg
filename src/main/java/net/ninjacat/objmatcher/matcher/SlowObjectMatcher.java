package net.ninjacat.objmatcher.matcher;

import io.vavr.control.Try;
import net.ninjacat.objmatcher.matcher.patterns.FieldPattern;
import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * Compares object to a pattern using reflection.
 *
 * @param <T> Type of the object to compare to
 */
public class SlowObjectMatcher<T> implements ObjectMatcher<T> {

    public static <A> SlowObjectMatcher<A> create() {
        return new SlowObjectMatcher<>();
    }

    public static <T> Predicate<T> forPattern(final ObjectPattern pattern) {
        return (obj) -> new SlowObjectMatcher<T>().matches(obj, pattern);
    }

    @Override
    public boolean matches(final T object, final ObjectPattern pattern) {
        if (!object.getClass().getSimpleName().equals(pattern.getClassName())) {
            return false;
        }
        return pattern.getFieldPatterns().stream()
                .map(matcher -> matchField(matcher, object))
                .allMatch(it -> Boolean.TRUE.compareTo(it) == 0);
    }

    private Boolean matchField(final FieldPattern matcher, final T object) {
        final Class<?> clazz = object.getClass();
        try {
            return tryMatchGetter(matcher, clazz, object).orElse(tryMatchField(matcher, clazz, object)).get();
        } catch (final Throwable e) {
            throw new MatchingException(String.format("Failed to read field '%s' in class '%s'",
                    matcher.getFieldName(), clazz.getSimpleName()), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Try<Boolean> tryMatchField(final FieldPattern matcher, final Class<?> clazz, final T object) {
        return Try.of(() -> {
            final Field fieldToMatch = clazz.getDeclaredField(matcher.getFieldName());
            fieldToMatch.setAccessible(true);
            final Object value = fieldToMatch.get(object);

            return matcher.matches(value);
        });
    }

    @SuppressWarnings("unchecked")
    private Try<Boolean> tryMatchGetter(final FieldPattern matcher, final Class<?> clazz, final T object) {
        final String fieldName = matcher.getFieldName();
        final String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return Try.of(() -> {
            final Method getter = clazz.getMethod(getterName);
            final Object value = getter.invoke(object);
            return matcher.matches(value);
        });

    }
}
