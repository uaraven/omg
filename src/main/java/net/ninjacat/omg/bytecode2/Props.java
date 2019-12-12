/*
 * omg: Params.java
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

package net.ninjacat.omg.bytecode2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Props {
    private final Map<String, Object> props = new ConcurrentHashMap<>();
    private final List<Runnable> postGenerators = new ArrayList<>();

    /**
     * Add a block of code to be run after match method is generated. It can be used to generate helper methods, static
     * code blocks etc.
     *
     * @param block {@link Runnable} to be executed at the final stage of code generation
     * @return This Props object for fluent call chaining
     */
    public Props postGenerator(final Runnable block) {
        this.postGenerators.add(block);
        return this;
    }

    public Props prop(final String name, final Object value) {
        this.props.put(name, value);
        return this;
    }

    public Map<String, Object> props() {
        return Collections.unmodifiableMap(props);
    }

    public Stream<Runnable> postGenerators() {
        return postGenerators.stream();
    }
}
