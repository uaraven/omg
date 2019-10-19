/*
 * omg: OmqlTypeConversion.java
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

package net.ninjacat.omg.omql;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.TypeUtils;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

final class OmqlTypeConversion {
    private OmqlTypeConversion() {
    }

    /**
     * Makes a best guess on a value type.
     * <p>
     * Can distinguish between int, long, double and string.
     *
     * @param value String containing a value
     * @return Value of correct type
     */
    private static Object toJavaType(final String value) {
        return Match(value).of(
                Case($(OmqlTypeConversion::isInteger), Integer::parseInt),
                Case($(OmqlTypeConversion::isLong), Long::parseLong),
                Case($(OmqlTypeConversion::isDouble), Double::parseDouble),
                Case($(OmqlTypeConversion::isBoolean), OmqlTypeConversion::toBoolean),
                Case($(OmqlTypeConversion::isString), OmqlTypeConversion::extractString),
                Case($(), s -> {
                    throw new OmqlParsingException("Unsupported value: %s", value);
                })
        );
    }

    /**
     * Converts string value into Java object of suitable type
     * <p>
     * Can distinguish between int, long, double and string.
     *
     * @param targetType Type to convert value to
     * @param value      String containing a value
     * @return Value of correct type
     */
    @SuppressWarnings("unchecked")
    static Object toJavaTypeStrict(final Class targetType, final String value) {
        return Try.of(() -> Match(targetType).of(
                Case($(is(null)), s -> toJavaType(value)),
                Case($(OmqlTypeConversion::isNumber), s -> OmqlTypeConversion.parseIntoNumeric(targetType, value)),
                Case($(OmqlTypeConversion::isBooleanType), s -> OmqlTypeConversion.toBoolean(value)),
                Case($(is(String.class)), s -> OmqlTypeConversion.extractStringChecked(value)),
                Case($(Class::isEnum), s -> Enum.valueOf(targetType, OmqlTypeConversion.extractStringChecked(value))),
                Case($((Predicate<Class>) Object.class::isAssignableFrom), s -> value),
                Case($(), s -> {
                    throw new TypeConversionException(value.getClass(), value, targetType);
                })
        )).getOrElseThrow(thr -> new TypeConversionException(thr, value, targetType));
    }

    private static boolean isLong(final String s) {
        return Try.of(() -> Long.parseLong(s))
                .filter(l -> l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
                .map(l -> true)
                .getOrElse(false);
    }

    private static boolean isInteger(final String s) {
        return Try.of(() -> Integer.parseInt(s))
                .map(l -> true)
                .getOrElse(false);
    }

    private static boolean isDouble(final String s) {
        return Try.of(() -> Double.parseDouble(s))
                .map(l -> true)
                .getOrElse(false);
    }

    private static boolean isBoolean(final String s) {
        return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }

    private static boolean isString(final String s) {
        return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"));
    }

    static String extractString(final String s) {
        return s.substring(1, s.length() - 1);
    }

    static String extractStringChecked(final String s) {
        if (isString(s)) {
            return s.substring(1, s.length() - 1);
        } else {
            throw new OmqlParsingException("Invalid string: " + s);
        }
    }

    private static <T> boolean isNumber(final Class<T> cls) {
        return TypeUtils.isIntegerType(cls) || TypeUtils.isFloatType(cls);
    }


    private static <T> boolean isBooleanType(final Class<T> cls) {
        return cls.equals(Boolean.class) || cls.equals(boolean.class);
    }

    private static <T> Number parseIntoNumeric(final Class<T> target, final String value) {
        final Number number = Match(value).of(
                Case($(OmqlTypeConversion::isInteger), Integer::parseInt),
                Case($(OmqlTypeConversion::isLong), Long::parseLong),
                Case($(OmqlTypeConversion::isDouble), Double::parseDouble),
                Case($(), s -> {
                    throw new OmqlParsingException("Cannot parse value '%s' as number", value);
                })
        );
        return TypeUtils.ensureNumericType(target, number);
    }

    private static boolean toBoolean(final String s) {
        if ("true".equalsIgnoreCase(s)) {
            return true;
        } else if ("false".equalsIgnoreCase(s)) {
            return false;
        } else {
            throw new OmqlParsingException("Cannot parse value '%s' as boolean", s);
        }
    }

}
