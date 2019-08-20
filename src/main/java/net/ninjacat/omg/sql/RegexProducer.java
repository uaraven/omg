package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class RegexProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {
    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final OmSqlParser.ConditionContext value) {
        final Object converted = toJavaType(value.literal_value().getText());
        if (converted instanceof String) {
            builder.property(property).regex((String) converted);
        } else {
            throw new SqlParsingException("Regex operation is only supported for strings");
        }
    }
}
