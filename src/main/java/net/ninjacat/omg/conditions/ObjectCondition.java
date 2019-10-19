/*
 * omg: ObjectCondition.java
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

package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;

import java.util.Objects;


public class ObjectCondition implements PropertyCondition<Condition> {
    private final String property;
    private final Condition value;

    public ObjectCondition(final String property, final Condition value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public Condition getValue() {
        return value;
    }

    @Override
    public String repr(final int level) {
        return Strings.indent("", level * 2) + property + " matches\n" + value.repr(level + 1);
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.MATCH;
    }

    @Override
    public String toString() {
        return repr();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ObjectCondition that = (ObjectCondition) o;
        return Objects.equals(property, that.property) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, value);
    }
}
