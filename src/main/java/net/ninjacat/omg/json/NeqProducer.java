package net.ninjacat.omg.json;

import net.ninjacat.omg.conditions.Conditions;

public class NeqProducer implements PropertyConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final Object value) {
        builder.property(property).neq(value);
    }
}
