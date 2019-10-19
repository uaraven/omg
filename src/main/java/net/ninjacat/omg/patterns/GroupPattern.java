/*
 * omg: GroupPattern.java
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

package net.ninjacat.omg.patterns;

import java.util.List;
import java.util.stream.Stream;

public abstract class GroupPattern<T> implements Pattern<T> {
    private final List<Pattern<T>> patterns;

    GroupPattern(final List<Pattern<T>> patterns) {
        this.patterns = io.vavr.collection.List.ofAll(patterns).asJava();
    }

    public Stream<Pattern<T>> getPatterns() {
        return patterns.stream();
    }
}
