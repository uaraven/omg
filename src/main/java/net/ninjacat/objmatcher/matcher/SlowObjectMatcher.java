package net.ninjacat.objmatcher.matcher;

import io.vavr.control.Try;
import net.ninjacat.objmatcher.matcher.patterns.FieldPattern;
import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;
import net.ninjacat.objmatcher.matcher.reflect.DefaultTypeConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Compares object to a pattern using reflection.
 *
 * @param <T> Type of the object to compare to
 */
public class SlowObjectMatcher<T> implements ObjectMatcher<T> {

    private final String defaultPackage;
    private TypeConverter typeConverter;
    private final ObjectPattern pattern;

    public static <A> SlowObjectMatcher<A> forPattern(final ObjectPattern pattern) {
        return new SlowObjectMatcher<>(pattern);
    }

    public SlowObjectMatcher(final ObjectPattern pattern) {
        this(pattern, "");
    }

    public SlowObjectMatcher(final ObjectPattern pattern, final String defaultPackage) {
        this(pattern, defaultPackage, new DefaultTypeConverter());
    }

    public SlowObjectMatcher(final ObjectPattern pattern, final String defaultPackage, final TypeConverter typeConverter) {
        this.pattern = pattern;
        this.defaultPackage = defaultPackage;
        this.typeConverter = typeConverter;
    }

    @Override
    public boolean test(final T object) {
        final Class clazz = getClassForMatching(pattern);

        if (!object.getClass().isAssignableFrom(clazz)) {
            return false;
        }
        return pattern.getFieldPatterns().stream()
                .map(matcher -> matchField(matcher, object))
                .allMatch(it -> Boolean.TRUE.compareTo(it) == 0);
    }

    private Class getClassForMatching(final ObjectPattern pattern) {
        final String className = this.defaultPackage.isBlank()
                ? pattern.getClassName()
                : String.join(".", this.defaultPackage, pattern.getClassName());
        return Try.of(() -> Class.forName(className))
                .getOrElseThrow((ex) -> new MatchingException(String.format("Class '%s' is not found", className), ex));
    }

    @SuppressWarnings("unchecked")
    private Boolean matchField(final FieldPattern matcher, final T object) {
        final Class<?> clazz = object.getClass();
        try {
            return tryMatchGetter(matcher, clazz, object)
                    .orElse(tryMatchField(matcher, clazz, object))
                    .map(value -> ensureValueType(value, matcher.getFieldType()))
                    .map((Function<Object, Boolean>) matcher::matches)
                    .get();
        } catch (final Throwable e) {
            throw new MatchingException(String.format("Failed to read field '%s' in class '%s'",
                    matcher.getFieldName(), clazz.getSimpleName()), e);
        }
    }

    private Object ensureValueType(final Object value, final Class fieldType) {
        final Class valueType = value.getClass();
        if (valueType.equals(fieldType)) {
            return value;
        } else {
            return typeConverter.convert(value).to(fieldType);
        }
    }

    private Try<Object> tryMatchField(final FieldPattern matcher, final Class<?> clazz, final T object) {
        return Try.of(() -> {
            final Field fieldToMatch = clazz.getDeclaredField(matcher.getFieldName());
            fieldToMatch.setAccessible(true);
            return fieldToMatch.get(object);
        });
    }

    private Try<Object> tryMatchGetter(final FieldPattern matcher, final Class<?> clazz, final T object) {
        final String fieldName = matcher.getFieldName();
        final String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return Try.of(() -> {
            final Method getter = clazz.getMethod(getterName);
            return getter.invoke(object);
        });

    }
}
