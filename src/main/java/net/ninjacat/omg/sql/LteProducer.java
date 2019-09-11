package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class LteProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmSqlParser.ConditionContext value) {
        final Object typedValue = toJavaType(value.literal_value().getText());
        validator.validate(property, typedValue);
        builder.or(cond -> cond
                .property(property).lt(typedValue)
                .property(property).eq(typedValue)
        );
    }
}
