package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

public class GteProducer implements SqlConditionProducer<OmSqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmSqlParser.ConditionContext value) {
        final Object typed = validator.validate(property, value.literal_value().getText());
        builder.or(cond -> {
                    cond
                            .property(property).gt(typed)
                            .property(property).eq(typed);
                }
        );
    }
}
