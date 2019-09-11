package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class LteProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmSqlParser.ConditionContext value) {
        final String text = value.literal_value().getText();
        validator.validate(property, text);
        final Object typedValue = toJavaType(text);
        builder.or(cond -> cond
                .property(property).lt(typedValue)
                .property(property).eq(typedValue)
        );
    }
}
