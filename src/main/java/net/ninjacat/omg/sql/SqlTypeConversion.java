package net.ninjacat.omg.sql;

import io.vavr.control.Try;
import net.ninjacat.omg.errors.SqlParsingException;

import static io.vavr.API.*;

final class SqlTypeConversion {
    private SqlTypeConversion() {
    }

    static boolean isLong(final String s) {
        return Try.of(() -> Long.parseLong(s))
                .filter(l -> l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
                .map(l -> true)
                .getOrElse(false);
    }

    static boolean isInteger(final String s) {
        return Try.of(() -> Integer.parseInt(s))
                .map(l -> true)
                .getOrElse(false);
    }

    static boolean isDouble(final String s) {
        return Try.of(() -> Double.parseDouble(s))
                .map(l -> true)
                .getOrElse(false);
    }

    static boolean isString(final String s) {
        return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"));
    }

    static String extractString(final String s) {
        return s.substring(1, s.length() - 1);
    }

    static boolean isNumber(final Class cls) {
        return cls.isAssignableFrom(Number.class);
    }

    static Number parseNumeric(final String value) {
        return Match(value).of(
                Case($(SqlTypeConversion::isInteger), Integer::parseInt),
                Case($(SqlTypeConversion::isLong), Long::parseLong),
                Case($(SqlTypeConversion::isDouble), Double::parseDouble),
                Case($(), s -> {
                    throw new SqlParsingException("Cannot parse value '%s' as number", value);
                })
        );
    }
}
