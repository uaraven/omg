/*
 * omg: Property.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.bytecode2;

import io.vavr.control.Try;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.errors.PatternException;
import org.objectweb.asm.commons.Method;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Immutable
public final class Property<T, P> {
    private final Class<T> owner;
    private final boolean isInterface;
    private final String propertyName;
    private final Class<P> type;
    private final Method method;

    private Property(final Class<T> owner, final boolean isInterface, final String propertyName, final Class<P> type, final Method method) {
        this.owner = owner;
        this.propertyName = propertyName;
        this.type = type;
        this.method = method;
        this.isInterface = isInterface;
    }

    static <T, P> Property<T, P> fromPropertyName(final String propertyName, final Class<T> cls) {
        final java.lang.reflect.Method getter = findMethod(cls, propertyName).orElseGet(() -> findGetter(cls, propertyName));
        final Method method = Method.getMethod(getter);
        final Class<P> propertyType = (Class<P>) getter.getReturnType();
        return new Property<>(cls, cls.isInterface(), propertyName, propertyType, method);
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

    public Class<T> getOwner() {
        return owner;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class getType() {
        return type;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public String toString() {
        return "Property{" + owner.getName() + '.' + propertyName + ": " + type + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        final Property<?, ?> property = (Property<?, ?>) o;
        return isInterface() == property.isInterface() &&
                getOwner().equals(property.getOwner()) &&
                getPropertyName().equals(property.getPropertyName()) &&
                getType().equals(property.getType()) &&
                getMethod().equals(property.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), isInterface(), getPropertyName(), getType(), getMethod());
    }
}
