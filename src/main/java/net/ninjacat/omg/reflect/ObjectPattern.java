/*
 * omg: ObjectPattern.java
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
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public class ObjectPattern<T> implements PropertyPattern<T> {

    private final Property property;
    private final Pattern objectPattern;

    ObjectPattern(final Property property, final Pattern objectPattern) {
        this.property = property;
        this.objectPattern = objectPattern;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(final T instance) {
        final Object propValue = getObjectValue(instance);
        return objectPattern.matches(propValue);
    }

    private Object getObjectValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .getOrElseThrow(err -> new CompilerException(err, "Failed to match property %s in %s", property, instance));
    }

    @Override
    public String toString() {
        return String.format("'%s' %% '%s'", property.toString(), objectPattern);
    }
}
