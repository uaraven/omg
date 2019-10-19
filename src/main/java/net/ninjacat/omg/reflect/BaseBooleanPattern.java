/*
 * omg: BaseBooleanPattern.java
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
import net.ninjacat.omg.patterns.PropertyPattern;

import java.lang.invoke.MethodHandle;

public abstract class BaseBooleanPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final boolean matchingValue;

    BaseBooleanPattern(final Property property, final boolean matchingValue) {
        this.property = property;
        this.matchingValue = matchingValue;
    }

    public boolean getMatchingValue() {
        return matchingValue;
    }

    @Override
    public boolean matches(final T instance) {
        final boolean propValue = getBooleanValue(instance);
        return compare(propValue);
    }

    @Override
    public String toString() {
        return String.format("'%s' %s '%s'", property.toString(), getComparatorAsString(), matchingValue);
    }

    private boolean getBooleanValue(final T instance) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> (Boolean) it)
                .getOrElseThrow(err -> new CompilerException(err, "Failed to match property %s in %s", property, instance));
    }

    protected abstract boolean compare(boolean propertyValue);

    protected abstract String getComparatorAsString();

}
