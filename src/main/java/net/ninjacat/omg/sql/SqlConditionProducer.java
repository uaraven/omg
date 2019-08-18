package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;

import static io.vavr.API.*;

@FunctionalInterface
public interface SqlConditionProducer {
    void create(Conditions.LogicalConditionBuilder builder, String property, String value);

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
                Case($(), s -> s)
        );
    }

}
