/*
 * omg: BasePropertyPattern.java
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

package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.patterns.PropertyPattern;

/**
 * Base class for matching property value.
 * @param <T> Type of object containing the property
 */
public abstract class BasePropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;

    public BasePropertyPattern(final Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    @Override
    public abstract boolean matches(final T instance);

}
