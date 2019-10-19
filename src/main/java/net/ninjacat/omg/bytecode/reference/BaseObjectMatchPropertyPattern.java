/*
 * omg: BaseObjectMatchPropertyPattern.java
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

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import net.ninjacat.omg.patterns.PropertyPattern;


/**
 * Base class for regex matching property value.
 * @param <T>
 */
public abstract class BaseObjectMatchPropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Pattern matchingPattern;

    protected BaseObjectMatchPropertyPattern(final Property property, final Object condition) {
        this.property = property;
        //noinspection unchecked
        this.matchingPattern = Patterns.compile(
                (Condition) condition,
                AsmPatternCompiler.forClass(property.getType()));
    }

    public Property getProperty() {
        return property;
    }

    public Pattern getMatchingValue() {
        return matchingPattern;
    }

    @Override
    public abstract boolean matches(final T instance);

}
