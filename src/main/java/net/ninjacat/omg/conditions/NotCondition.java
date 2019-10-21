/*
 * omg: NotCondition.java
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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.ninjacat.omg.utils.Strings;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableNotCondition.class)
public abstract class NotCondition implements Condition {
    public abstract Condition getChild();

    @Override
    public String repr(final int level) {
        return Strings.indent("NOT ", level * 2) + getChild().repr(0) + "\n";
    }

    @Override
    public String toString() {
        return repr();
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.LOGIC;
    }
}
