package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.omql.parser.OmqlParser;

public class GteProducer implements OmqlConditionProducer<OmqlParser.ConditionContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final QueryContext context,
                       final OmqlParser.ConditionContext value) {
        final Object typed = context.validator().validate(property, value.literal_value().getText());
        builder.or(cond -> cond
                .property(property).gt(typed)
                .property(property).eq(typed)
        );
    }
}
