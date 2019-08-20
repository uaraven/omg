package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class NeqProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final OmSqlParser.ConditionContext value) {
        builder.property(property).neq(toJavaType(value.literal_value().getText()));
    }
}
