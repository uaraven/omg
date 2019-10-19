/*
 * omg: RegexBasePropertyPattern.java
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

import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;

import java.util.regex.Pattern;

/**
 * Base class for regex matching property value.
 * @param <T>
 */
public abstract class RegexBasePropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Pattern regex;

    public RegexBasePropertyPattern(final Property property, final Object matchingValue) {
        this.property = property;
        if (matchingValue instanceof String) {
            this.regex = Pattern.compile((String) matchingValue);
        } else {
            throw new CompilerException("Value must be string for REGEX condition, got '%s' instead", matchingValue);
        }
    }

    public Property getProperty() {
        return property;
    }

    public Pattern getMatchingValue() {
        return regex;
    }

    @Override
    public abstract boolean matches(final T instance);

}
