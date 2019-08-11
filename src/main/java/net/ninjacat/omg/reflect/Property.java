package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.errors.PatternException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Immutable
public final class Property<T> {
    private final Class<T> owner;
    private final String propertyName;
    private final Class type;
    private final Class widenedType;
    private final MethodHandle getterMethod;

    private Property(final Class<T> owner, final String propertyName, final Class type, final Class widenedType, final MethodHandle getterMethod) {
        this.owner = owner;
        this.propertyName = propertyName;
        this.type = type;
        this.widenedType = widenedType;
        this.getterMethod = getterMethod;
    }

    static <T> Property<T> fromPropertyName(final String propertyName, final Class<T> cls) {
        final Method getter = findMethod(cls, propertyName).orElseGet(() -> findGetter(cls, propertyName));
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

    private static String toPascalCase(final String name) {
        return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
    }

    public Class<T> getOwner() {
        return owner;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class getType() {
        return type;
    }

    public Class getWidenedType() {
        return widenedType;
    }

    public MethodHandle getGetterMethod() {
        return getterMethod;
    }

    @Override
    public String toString() {
        return "Property{" + owner.getName() + '.' + propertyName + ": " + type + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Property<?> property = (Property<?>) o;
        return Objects.equals(owner, property.owner) &&
                Objects.equals(propertyName, property.propertyName) &&
                Objects.equals(type, property.type) &&
                Objects.equals(widenedType, property.widenedType) &&
                Objects.equals(getterMethod, property.getterMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, propertyName, type, widenedType, getterMethod);
    }
}
