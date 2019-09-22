package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.sql.parser.OmSqlParser;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

@FunctionalInterface
public interface SqlConditionProducer<T extends OmSqlParser.ExprContext> {
    void create(Conditions.LogicalConditionBuilder builder, String property, TypeValidator validator, T value);

    /**
     * Makes a best guess on a value type.
     * <p>
     * Can distinguish between int, long, double and string.
     *
     * @param value String containing a value
     * @return Value of correct type
     */
    default Object toJavaType(final String value) {
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
    default Object toJavaTypeStrict(final Class targetType, final String value) {
        return Match(targetType).of(
                Case($(is(null)), s -> toJavaType(value)),
                Case($(SqlTypeConversion::isNumber), s -> SqlTypeConversion.parseNumeric(value)),
                Case($(is(String.class)), s -> SqlTypeConversion.extractStringChecked(value)),
                Case($(Class::isEnum), s -> Enum.valueOf(targetType, value)),
                Case($((Predicate<Class>) Object.class::isAssignableFrom), s -> value),
                Case($(), s -> {
                    throw new SqlParsingException("Cannot handle type %s", targetType);
                })
        );
    }
}
