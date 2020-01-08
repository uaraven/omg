/*
 * omg: PrimitiveIntegerCompatibilityValidator.java
 *
 * Copyright 2020 Oleksiy Voronin <me@ovoronin.info>
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

package net.ninjacat.omg.bytecode2.types;

import net.ninjacat.omg.conditions.PropertyCondition;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NumberCompatibilityValidator<T> implements TypeCompatibilityValidator {
    private static final Map<Class<?>, TypeCompatibilityValidator> CACHE = new ConcurrentHashMap<>();

    private final Class<?> targetClass;

    public static <T> TypeCompatibilityValidator forClass(final Class<T> cls) {
        return CACHE.computeIfAbsent(cls, NumberCompatibilityValidator::new);
    }

    public NumberCompatibilityValidator(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public <V> boolean canBeAssigned(final PropertyCondition<V> condition) {
        final Class<?> valueType = condition.getValue().getClass();
        return (condition.getValue() instanceof Collection && isValidCollection((Collection<?>) condition.getValue()))
                || valueType.equals(targetClass);
    }

    private boolean isValidCollection(final Collection<?> c) {
        return c.stream().allMatch(i -> i.getClass().equals(targetClass));
    }
}
