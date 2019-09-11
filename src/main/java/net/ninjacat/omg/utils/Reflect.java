package net.ninjacat.omg.utils;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.PatternException;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

/**
 * Reflection utilities
 */
public final class Reflect {
    private Reflect() {
    }

    @SuppressWarnings("unchecked")
    private static Optional<Method> findMethod(final Class cls, final String propertyName) {
        return Try
                .of(() -> cls.getMethod(propertyName))
                .filter(m -> !m.getReturnType().equals(Void.class) && m.getParameterCount() == 0).toJavaOptional();
    }

    @SuppressWarnings("unchecked")
    private static Method findGetter(final Class cls, final String propertyName) {
        final String propertyNamePascal = toPascalCase(propertyName);
        final Try<Method> method = Try
                .of(() -> cls.getMethod("get" + propertyNamePascal))
                .orElse(Try.of(() -> cls.getMethod("is" + propertyNamePascal)))
                .filter(m -> !m.getReturnType().equals(Void.class) && m.getParameterCount() == 0);
        return method.getOrElseThrow((ex) ->
                new PatternException(ex, "Cannot find accessor method for property '%s' in class '%s'",
                        propertyName, cls.getName()));
    }

    /**
     * Gets a callable that can be used to retrieve property value. Callable here is either method with
     * name matching the property name or a getter for the property (with the name starting with "get" or "is")
     *
     * @param propertyName Name of the property
     * @param cls          Class containing the property
     * @param <T>          Type of the object
     * @return Method
     * @throws PatternException if given callable is not found
     */
    public static <T> Method getCallable(final String propertyName, final Class<T> cls) {
        return findMethod(cls, propertyName).orElseGet(() -> findGetter(cls, propertyName));
    }

    private static String toPascalCase(final String name) {
        return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
    }
}
