/*
 * omg: FloatBasePropertyPattern.java
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

package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

/**
 * Base class for {@link net.ninjacat.omg.patterns.PropertyPattern}s for long properties
 *
 * @param <T>
 */
public abstract class FloatBasePropertyPattern<T> extends BasePropertyPattern<T> {

    private final float matchingValue;

    protected FloatBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueAsFloat(matchingValue);
    }

    public float getMatchingValue() {
        return matchingValue;
    }

    private float getMatchingValueAsFloat(final T mv) {
        if (mv instanceof Number) {
            return ((Number) mv).floatValue();
        } else {
            throw new TypeConversionException(mv.getClass(), mv, float.class);
        }
    }

}
