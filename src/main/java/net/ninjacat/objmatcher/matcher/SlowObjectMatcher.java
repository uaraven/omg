package net.ninjacat.objmatcher.matcher;

import io.vavr.control.Try;
import net.ninjacat.objmatcher.matcher.errors.MatchingException;
import net.ninjacat.objmatcher.matcher.patterns.FieldPattern;
import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;
import net.ninjacat.objmatcher.matcher.reflect.DefaultTypeConverter;
import net.ninjacat.objmatcher.matcher.reflect.ObjectMetadata;
import net.ninjacat.objmatcher.matcher.reflect.PropertyMetadata;

import java.util.Set;
import java.util.function.Function;

/**
 * Compares object to a pattern using reflection.
 *
 * @param <T> Type of the object to compare to
 */
public class SlowObjectMatcher<T> implements ObjectMatcher<T> {

    private static final Set<Class> INT_CLASSES = Set.of(
            Integer.class,
            Long.class,
            Short.class,
            Byte.class,
            Character.class
    );
    private static final Set<Class> FLOAT_CLASSES = Set.of(
            Float.class,
            Double.class
    );

    private final String defaultPackage;
    private final ObjectMetadata objectMetadata;
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
        this.objectMetadata = new ObjectMetadata(getClassForMatching(pattern));
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
            final String fieldName = matcher.getFieldName();
            final PropertyMetadata property = objectMetadata.getFieldData(fieldName);

            return tryMatchGetter(property, object)
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
        final Class expandedFieldType = expandClass(fieldType);
        if (valueType.equals(fieldType)) {
            return value;
        } else {
            return typeConverter.convert(value).to(expandedFieldType);
        }
    }

    private Class expandClass(final Class<?> aClass) {
        if (INT_CLASSES.contains(aClass)) {
            return Long.class;
        } else if (FLOAT_CLASSES.contains(aClass)) {
            return Double.class;
        } else {
            return aClass;
        }
    }

    private Try<Object> tryMatchGetter(PropertyMetadata propery, final T object) {
        return Try.of(() -> propery.getGetterMethod().invoke(object));

    }
}
