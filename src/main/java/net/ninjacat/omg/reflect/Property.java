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

package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.errors.PatternException;
import net.ninjacat.omg.utils.Reflect;
import net.ninjacat.omg.utils.TypeUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Objects;

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
        final Method getter = Reflect.getCallable(propertyName, cls);
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
