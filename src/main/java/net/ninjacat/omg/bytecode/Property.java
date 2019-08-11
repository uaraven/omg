package net.ninjacat.omg.bytecode;

import io.vavr.control.Try;
import lombok.Value;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.errors.PatternException;
import org.objectweb.asm.commons.Method;

import java.util.Locale;
import java.util.Optional;

@Value
@Immutable
public class Property<T> {
    final Class<T> owner;
    final String propertyName;
    final Class type;
    final Method method;

    static <T> Property<T> fromPropertyName(final String propertyName, final Class<T> cls) {
        final java.lang.reflect.Method getter = findMethod(cls, propertyName).orElseGet(() -> findGetter(cls, propertyName));
        final Method method = Method.getMethod(getter);
        final Class propertyType = getter.getReturnType();
        return new Property<>(cls, propertyName, propertyType, method);
    }


    @SuppressWarnings("unchecked")
    private static Optional<java.lang.reflect.Method> findMethod(final Class cls, final String propertyName) {
        return Try
                .of(() -> cls.getMethod(propertyName))
                .filter(m -> !m.getReturnType().equals(Void.class) && m.getParameterCount() == 0).toJavaOptional();
    }

    @SuppressWarnings("unchecked")
    private static java.lang.reflect.Method findGetter(final Class cls, final String propertyName) {
        final String propertyNamePascal = toPascalCase(propertyName);
        final Try<java.lang.reflect.Method> method = Try
                .of(() -> cls.getMethod("get" + propertyNamePascal))
                .orElse(Try.of(() -> cls.getMethod("is" + propertyNamePascal)))
                .filter(m -> !m.getReturnType().equals(Void.class) && m.getParameterCount() == 0);
        return method.getOrElseThrow((ex) ->
                new PatternException(ex, "Cannot find accessor method for property '%s' in class '%s'",
                        propertyName, cls.getName()));
    }

    private static String toPascalCase(final String name) {
        return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
    }

    @Override
    public String toString() {
        return "Property{" + owner.getName() + '.' + propertyName + ": " + type + '}';
    }
}
