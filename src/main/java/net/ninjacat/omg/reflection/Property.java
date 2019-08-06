package net.ninjacat.omg.reflection;

import io.vavr.control.Try;
import lombok.Value;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.errors.PatternException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Locale;

@Value
@Immutable
public class Property<T> {
    final Class<T> owner;
    final String propertyName;
    final Class type;
    final Class widenedType;
    final MethodHandle getterMethod;


    static <T> Property<T> fromPropertyName(final String propertyName, final Class<T> cls) {
        final Method getter = findGetter(cls, propertyName);
        final Class propertyType = getter.getReturnType();
        final Class widenedType = TypeUtils.widen(propertyType);
        final MethodType methodType = MethodType.methodType(getter.getReturnType());
        final MethodHandle handle = Try.of(() -> MethodHandles.lookup().findVirtual(cls, getter.getName(), methodType))
                .getOrElseThrow(err -> new PatternException(
                        err,
                        "Failed to get handle for accessor method for property '%s' in class '%s'",
                        propertyName, cls.getName()));
        return new Property<>(cls, propertyName, propertyType, widenedType, handle);
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

    private static String toPascalCase(final String name) {
        return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
    }

    @Override
    public String toString() {
        return "Property{" + owner.getName() + '.' + propertyName + ": " + type + '}';
    }
}
