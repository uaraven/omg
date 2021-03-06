/*
 * omg: InPropertyPattern.java
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

package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class InPropertyPattern<T, E> extends BasePropertyPattern<T> {
    private final Collection<E> matchingValue;

    protected InPropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    private Collection<E> getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return Collections.emptySet();
        } else if (mv instanceof Collection) {
            //noinspection unchecked
            return Collections.unmodifiableCollection((Collection<E>) mv);
        } else {
            throw new TypeConversionException(mv.getClass(), mv, List.class);
        }
    }

    public Collection<E> getMatchingValue() {
        return matchingValue;
    }

    public boolean isInCollection(final E propertyValue, final Collection<E> matchingValue) {
        return matchingValue.stream().anyMatch(item -> item.equals(propertyValue));
    }
}
