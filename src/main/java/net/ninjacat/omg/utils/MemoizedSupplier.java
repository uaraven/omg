/*
 * omg: MemoizedSupplier.java
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

package net.ninjacat.omg.utils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MemoizedSupplier<T> implements Supplier<T> {
    private final AtomicReference<T> instance = new AtomicReference<>();
    private final Supplier<T> wrapped;

    public MemoizedSupplier(final Supplier<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public T get() {
        return instance.getAndUpdate(current -> current == null ? wrapped.get() : current);
    }
}
