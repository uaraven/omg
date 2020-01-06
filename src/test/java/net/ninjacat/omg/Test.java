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
import java.util.stream.Collectors;

public class Test {


    public static short getByte() {
        return 1;
    }

    public static void convert() {
        int i = Integer.valueOf(getByte());
    }

    public static void toStringTest() {
        final Object o = new Integer(123);
        o.toString();
    }

    public static boolean matchesEnum(String s) {
        try {
            return En.E1 == En.valueOf(s);
        } catch (Exception e) {
            return false;
        }
    }

    public static Collection<Enum> makeEnumArray(String s) {
        return Arrays.asList(new En[]{En.E1, En.E2}).stream().collect(Collectors.toSet());
    }

    public static class TestClass {

    }

    public enum En {
        E1, E2, E3, E4;
    }
}

