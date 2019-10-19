/*
 * omg: LongBasePropertyPattern.java
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

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for Long properties
 *
 * @param <T>
 */
public abstract class LongBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Long matchingValue;

    protected LongBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    public Long getMatchingValue() {
        return matchingValue;
    }

    private Long getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return null;
        } else if (mv instanceof Number) {
            return ((Number) mv).longValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, Long.class);
        }
    }
}
