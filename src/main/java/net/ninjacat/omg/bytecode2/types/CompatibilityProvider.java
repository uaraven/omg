/*
 * omg: CompatibilityProvider.java
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

import io.vavr.Tuple;
import io.vavr.collection.HashMap;

import java.util.Map;

public final class CompatibilityProvider {

    private static final Map<Class<?>, TypeCompatibilityValidator> VALIDATORS = HashMap.<Class<?>, TypeCompatibilityValidator>ofEntries(
            Tuple.of(int.class, NumberCompatibilityValidator.forClass(Integer.class)),
            Tuple.of(Integer.class, NumberCompatibilityValidator.forClass(Integer.class)),
            Tuple.of(long.class, NumberCompatibilityValidator.forClass(Long.class)),
            Tuple.of(Long.class, NumberCompatibilityValidator.forClass(Long.class)),
            Tuple.of(byte.class, NumberCompatibilityValidator.forClass(Byte.class)),
            Tuple.of(Byte.class, NumberCompatibilityValidator.forClass(Byte.class)),
            Tuple.of(short.class, NumberCompatibilityValidator.forClass(Short.class)),
            Tuple.of(Short.class, NumberCompatibilityValidator.forClass(Short.class)),
            Tuple.of(char.class, NumberCompatibilityValidator.forClass(Character.class)),
            Tuple.of(Character.class, NumberCompatibilityValidator.forClass(Character.class)),
            Tuple.of(double.class, NumberCompatibilityValidator.forClass(Double.class)),
            Tuple.of(Double.class, NumberCompatibilityValidator.forClass(Double.class)),
            Tuple.of(float.class, NumberCompatibilityValidator.forClass(Float.class)),
            Tuple.of(Float.class, NumberCompatibilityValidator.forClass(Float.class)),
            Tuple.of(boolean.class, NumberCompatibilityValidator.forClass(Boolean.class)),
            Tuple.of(Boolean.class, NumberCompatibilityValidator.forClass(Boolean.class))
    ).toJavaMap();

    private CompatibilityProvider() {
    }

    public static TypeCompatibilityValidator forClass(final Class<?> klass) {
        return VALIDATORS.get(klass);
    }
}
