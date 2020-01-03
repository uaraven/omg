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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Test {
    public Collection<Integer> getInts3() {
        return Arrays.stream(new int[]{41, 42, 43, 44, 8012454}).boxed().collect(Collectors.toSet());
    }

    public Collection<Long> getLongStream() {
        return Arrays.stream(new long[]{41L, 0L, 4L, 8L, 8012454L}).boxed().collect(Collectors.toSet());
    }

    public int getDoubleCmp(double a, double b) {
        if (a > b) {
            return 1;
        } else {
            return 2;
        }
    }

    public Collection<String> getStrings() {
        return Arrays.stream(new String[]{"a", "b", "c", "d"}).collect(Collectors.toSet());
    }

    public static Pattern getPattern(final String value) {
        return Pattern.compile(value);
    }

    public static boolean matches(final TestClass instance) {
        return instance != null;
    }


    public static boolean test() {
        final Pattern ptrn = getPattern(".*");
        return ptrn.matcher("abc").matches();
    }

    public static class TestClass {

    }
}

