package net.ninjacat.omg.sql;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.TypeUtils;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

final class SqlTypeConversion {
    private SqlTypeConversion() {
    }

    /**
     * Makes a best guess on a value type.
     * <p>
     * Can distinguish between int, long, double and string.
     *
     * @param value String containing a value
     * @return Value of correct type
     */
    static Object toJavaType(final String value) {
        return Match(value).of(
                Case($(SqlTypeConversion::isInteger), Integer::parseInt),
                Case($(SqlTypeConversion::isLong), Long::parseLong),
                Case($(SqlTypeConversion::isDouble), Double::parseDouble),
                Case($(SqlTypeConversion::isString), SqlTypeConversion::extractString),
                Case($(), s -> {
                    throw new SqlParsingException("Unsupported value: %s", value);
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
                Case($(SqlTypeConversion::isNumber), s -> SqlTypeConversion.parseIntoNumeric(targetType, value)),
                Case($(is(String.class)), s -> SqlTypeConversion.extractStringChecked(value)),
                Case($(Class::isEnum), s -> Enum.valueOf(targetType, SqlTypeConversion.extractStringChecked(value))),
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
            throw new SqlParsingException("Invalid string: " + s);
        }
    }

    private static <T> boolean isNumber(final Class<T> cls) {
        return TypeUtils.isIntegerType(cls) || TypeUtils.isFloatType(cls);
    }

    private static <T> Number parseIntoNumeric(final Class<T> target, final String value) {
        final Number number = Match(value).of(
                Case($(SqlTypeConversion::isInteger), Integer::parseInt),
                Case($(SqlTypeConversion::isLong), Long::parseLong),
                Case($(SqlTypeConversion::isDouble), Double::parseDouble),
                Case($(), s -> {
                    throw new SqlParsingException("Cannot parse value '%s' as number", value);
                })
        );
        return TypeUtils.ensureNumericType(target, number);
    }

}
