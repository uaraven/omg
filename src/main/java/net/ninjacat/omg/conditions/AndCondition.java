/*
 * omg: AndCondition.java
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
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AndCondition implements LogicalCondition {
    @Override
    public abstract List<Condition> getChildren();

    @Override
    public String repr(final int level) {
        final StringBuilder sb = new StringBuilder(Strings.indent("AND {", level * 2)).append("\n");
        getChildren().forEach(item -> sb.append(item.repr(level + 1)).append("\n"));
        return sb.append(Strings.indent("}", level * 2)).append("\n").toString();
    }

    @Override
    public String toString() {
        return repr();
    }
}
