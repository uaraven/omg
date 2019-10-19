/*
 * omg: NotPattern.java
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

public class NotPattern<T> implements Pattern<T> {

    private final Pattern<T> childPattern;

    public NotPattern(final Pattern<T> childPattern) {
        this.childPattern = childPattern;
    }

    @Override
    public boolean matches(final T instance) {
        return !childPattern.matches(instance);
    }
}
