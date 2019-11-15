/*
 * omg: ComparisonCondition.java
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

public abstract class ComparisonCondition<P> implements PropertyCondition<P> {
    private final String field;
    private final P value;

    ComparisonCondition(final String property, final P value) {
        this.field = property;
        this.value = value;
    }

    @Override
    public String getProperty() {
        return field;
    }

    @Override
    public P getValue() {
        return value;
    }

    @Override
    public String toString() {
        return repr();
    }

    @Override
    public String repr(final int level) {
        return Strings.indent("", level * 2) + "'" + getProperty() + "' " + operatorRepr() + " '" + getValue() + "'";
    }

    protected abstract String operatorRepr();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonCondition)) return false;
        final ComparisonCondition<?> that = (ComparisonCondition<?>) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, getValue());
    }
}
