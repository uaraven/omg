/*
 * omg: Test.java
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

package net.ninjacat.omg;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Test {
    private static final Collection<Integer> ints =
            io.vavr.collection.HashSet.of(10, 20, 30, 40, 50).toJavaSet();


    public Collection<Integer> getInts1() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(10, 20, 30, 40, 50)));
    }

    public Collection<Integer> getInts2() {
        final HashSet<Integer> set = new HashSet<>();
        set.add(10);
        set.add(20);
        set.add(30);
        set.add(40);
        set.add(454350);
        return Collections.unmodifiableSet(set);
    }

    public Collection<Integer> getInts3() {
        return Arrays.stream(new int[]{10, 20, 30, 40, 50, 60, 70, 555555}).boxed().collect(Collectors.toSet());
    }

    public Collection<Integer> getInts4() {
        return io.vavr.collection.HashSet.of(10, 20, 30, 40, 50).toJavaSet();
    }

    public Collection<Integer> getInts5() {
        return ints;
    }
}

