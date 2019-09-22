package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.sql.parser.OmSqlParser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Producer for SQL condition IN (list)
 */
public class InProducer implements SqlConditionProducer<OmSqlParser.InExprContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmSqlParser.InExprContext value) {
        AntlrTools.assertError(value.list().children);
        final List<Object> values = value.list().literal_value().stream()
                .map(it -> validator.validate(property, it.getText()))
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            builder.property(property).in(Collections.emptyList());
        } else {
            final Class<?> firstClass = values.get(0).getClass();
            if (!values.stream().allMatch(it -> firstClass.isAssignableFrom(it.getClass()))) {
                throw new SqlParsingException("All elements of IN operation should be of same type");
            }
            final List<Object> items = values.stream().map(firstClass::cast).collect(Collectors.toList());
            builder.property(property).in(items);
        }
    }
}
