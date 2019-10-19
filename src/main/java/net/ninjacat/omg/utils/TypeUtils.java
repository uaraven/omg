/*
 * omg: TypeUtils.java
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

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import net.ninjacat.omg.errors.MatcherException;
import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.reflect.Property;

import java.lang.invoke.MethodHandle;
import java.util.function.Function;

import static io.vavr.API.*;

/**
 * Basic type conversions
 */
public final class TypeUtils {
    private TypeUtils() {
    }

    private static final Set<Class> INT_CLASSES = HashSet.of(
            Integer.class,
            Long.class,
            Short.class,
            Byte.class,
            Character.class,
            int.class,
            long.class,
            byte.class,
            short.class,
            char.class
    );
    private static final Set<Class> FLOAT_CLASSES = HashSet.of(
            Float.class,
            Double.class,
            float.class,
            double.class
    );

    private static final HashMap<Function<Class<?>, Boolean>, Function<Number, Number>> NUMBER_CONVERTER = HashMap.of(
            TypeUtils::isByteClass, Number::byteValue,
            TypeUtils::isIntClass, Number::intValue,
            TypeUtils::isShortClass, Number::shortValue,
            TypeUtils::isLongClass, Number::longValue,
            TypeUtils::isFloatClass, Number::floatValue,
            TypeUtils::isDoubleClass, Number::doubleValue
    );

    /**
     * Widens type to one supported by matcher.
     * <p>
     * int, short, byte, char -&gt; long
     * <p>
     * float -&gt; double
     * <p>
     * All other types remain unchanged.
     *
     * @param aClass Type to widen
     * @return Widened type
     */
    public static Class widen(final Class<?> aClass) {
        if (INT_CLASSES.contains(aClass)) {
            return Long.class;
        } else if (FLOAT_CLASSES.contains(aClass)) {
            return Double.class;
        } else if (aClass.equals(boolean.class)) {
            return Boolean.class;
        } else {
            return aClass;
        }
    }

    /**
     * Converts value to a basic type which is {@link Long} for all integer types and {@link Double} for floating-point.
     * <p>
     * All other types remain unchanged
     *
     * @param value Value to convert to basic type
     * @return Value converted to basic type, if possible, otherwise original value
     */
    public static Object convertToBasicType(final Object value) {
        return Match(value).of(
                Case($(TypeUtils::isInteger), i -> (long) (int) i),
                Case($(TypeUtils::isShort), i -> (long) (short) i),
                Case($(TypeUtils::isByte), i -> (long) (byte) i),
                Case($(TypeUtils::isChar), i -> (long) (char) i),
                Case($(TypeUtils::isFloat), i -> (double) (float) i),
                Case($(), Function.identity())
        );
    }

    public static <T, R> R getAsType(final T instance, final Property<T> property, final Class<? extends R> valueType) {
        final MethodHandle getter = property.getGetterMethod();
        return Try.of(() -> getter.invoke(instance))
                .map(it -> valueType.cast(TypeUtils.convertToBasicType(it)))
                .getOrElseThrow(err -> new MatcherException(err, "Failed to match property %s in %s", property, instance));
    }

    private static boolean isInteger(final Object o) {
        return o instanceof Integer || o.getClass().equals(int.class);
    }

    private static boolean isShort(final Object o) {
        return o instanceof Short || o.getClass().equals(short.class);
    }

    private static boolean isByte(final Object o) {
        return o instanceof Byte || o.getClass().equals(byte.class);
    }

    private static boolean isChar(final Object o) {
        return o instanceof Character || o.getClass().equals(char.class);
    }

    private static boolean isBooleanFloat(final Object o) {
        return o instanceof Boolean || o.getClass().equals(boolean.class);
    }

    private static boolean isFloat(final Object o) {
        return o instanceof Float || o.getClass().equals(float.class);
    }

    private static <T> boolean isIntClass(final Class<T> o) {
        return o.equals(Integer.class) || o.equals(int.class);
    }

    private static <T> boolean isLongClass(final Class<T> o) {
        return o.equals(Long.class) || o.equals(long.class);
    }

    private static <T> boolean isShortClass(final Class<T> o) {
        return o.equals(Short.class) || o.equals(short.class);
    }

    private static <T> boolean isByteClass(final Class<T> o) {
        return o.equals(Byte.class) || o.equals(byte.class);
    }

    private static <T> boolean isCharClass(final Class<T> o) {
        return o.equals(Character.class) || o.equals(char.class);
    }

    private static <T> boolean isFloatClass(final Class<T> o) {
        return o.equals(Float.class) || o.equals(float.class);
    }

    private static <T> boolean isDoubleClass(final Class<T> o) {
        return o.equals(Double.class) || o.equals(double.class);
    }

    /**
     * Converts {@link Number} value into requested numeric type
     *
     * @param type  Requested type
     * @param value Value to convert
     * @param <T>   Type parameter
     * @return {@link Number} converted into requested type
     */
    public static <T> Number ensureNumericType(final Class<T> type, final Number value) {
        if (isCharClass(type)) {
            throw new TypeConversionException(value.getClass(), value, type);
        } else {
            return NUMBER_CONVERTER.find(k -> k._1().apply(type)).get()._2().apply(value);
        }
    }

    /**
     * Checks whether class is an integer number (i.e. int, short, byte, long, char or corresponding boxed type)
     *
     * @param cls Class to test
     * @param <T> Class parameter
     * @return true if class is integer number
     */
    public static <T> boolean isIntegerType(final Class<T> cls) {
        return INT_CLASSES.contains(cls);
    }

    /**
     * Checks whether class is an floating point number (i.e. float, double or corresponding boxed type)
     *
     * @param cls Class to test
     * @param <T> Class parameter
     * @return true if class is floating point number
     */
    public static <T> boolean isFloatType(final Class<T> cls) {
        return FLOAT_CLASSES.contains(cls);
    }
}
