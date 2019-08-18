package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;

public class GtProducer implements SqlConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final String value) {
        builder.property(property).gt(toJavaType(value));
    }
}
