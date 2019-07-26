package net.ninjacat.objmatcher.matcher;

import io.vavr.control.Try;
import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.matcher.errors.MatcherException;
import net.ninjacat.objmatcher.matcher.matchers.ObjectPattern;
import net.ninjacat.objmatcher.matcher.reflect.DefaultObjectProperties;
import net.ninjacat.objmatcher.matcher.reflect.DefaultTypeConverter;
import net.ninjacat.objmatcher.matcher.reflect.Property;
import net.ninjacat.objmatcher.matcher.reflect.PropertyMatcher;

import java.util.function.Function;

/**
 * Compares object to a pattern using reflection.
 *
 * @param <T> Type of the object to compare to
 */
@Immutable
public class SlowObjectMatcher<T> implements ObjectMatcher<T> {

    private final String defaultPackage;
    private final ObjectProperties objectMetadata;
    private final TypeConverter typeConverter;
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
        this.objectMetadata = new DefaultObjectProperties(getClassForMatching(pattern));
    }

    @Override
    public boolean test(final T object) {
        final Class clazz = getClassForMatching(pattern);

        if (!object.getClass().isAssignableFrom(clazz)) {
            return false;
        }
        return pattern.getPropertyMatchers().stream()
                .map(matcher -> matchField(matcher, object))
                .allMatch(it -> Boolean.TRUE.compareTo(it) == 0);
    }

    private Class getClassForMatching(final ObjectPattern pattern) {
        final String className = this.defaultPackage.trim().isEmpty()
                ? pattern.getClassName()
                : String.join(".", this.defaultPackage, pattern.getClassName());
        return Try.of(() -> Class.forName(className))
                .getOrElseThrow((ex) -> new MatcherException(ex, "Class '%s' is not found", className));
    }

    @SuppressWarnings("unchecked")
    private Boolean matchField(final PropertyMatcher matcher, final T object) {
        final Class<?> clazz = object.getClass();
        try {
            final String fieldName = matcher.getFieldName();
            final Property property = objectMetadata.getProperty(fieldName);

            return tryMatchGetter(property, object)
                    .map(value -> ensureValueType(value, matcher.getFieldType()))
                    .map((Function<Object, Boolean>) matcher::matches)
                    .get();
        } catch (final Throwable e) {
            throw new MatcherException(e, "Failed to read field '%s' in class '%s'",
                    matcher.getFieldName(), clazz.getSimpleName());
        }
    }


    private Object ensureValueType(final Object value, final Class fieldType) {
        final Class valueType = value.getClass();
        final Class expandedFieldType = expandClass(fieldType);
        if (valueType.equals(fieldType)) {
            return value;
        } else {
            return typeConverter.convert(value).to(expandedFieldType);
        }
    }


    private Try<Object> tryMatchGetter(final Property property, final T object) {
        return Try.of(() -> property.getGetterMethod().invoke(object));
    }
}
