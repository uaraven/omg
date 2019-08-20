package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class EqProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final OmSqlParser.ConditionContext value) {
        final String text = value.literal_value().getText();
        builder.property(property).eq(toJavaType(text));
    }
}
