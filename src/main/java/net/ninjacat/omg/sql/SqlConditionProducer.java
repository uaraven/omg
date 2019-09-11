package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.sql.parser.OmSqlParser;

import static io.vavr.API.*;

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

}
