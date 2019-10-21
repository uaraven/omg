/*
 * omg: Condition.java
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Basic interface for condition.
 *
 * Conditions are simple criteria comparing named field to a value.
 */
public interface Condition {
    /**
     * Condition representation for pretty printing
     *
     * @param level Depth level
     * @return String representation of condition
     */
    String repr(int level);

    /**
     * Default representation without indentation
     *
     * @return String representation of condition
     */
    default String repr() {
        return repr(0);
    }

    @JsonIgnore
    ConditionMethod getMethod();

}
