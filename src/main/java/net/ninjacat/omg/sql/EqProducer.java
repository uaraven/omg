package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class EqProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final TypeValidator validator, final OmSqlParser.ConditionContext value) {
        String text = value.literal_value().getText();
        validator.validate(property, text);
        final Object typed = toJavaType(text);
        builder.property(property).eq(typed);
    }
}
