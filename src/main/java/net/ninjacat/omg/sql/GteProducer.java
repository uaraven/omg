package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;

public class GteProducer implements SqlConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final String value) {
        builder.or(cond -> cond
                .property(property).gt(toJavaType(value))
                .property(property).eq(toJavaType(value))
        );
    }
}
