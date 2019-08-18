package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;

public class LteProducer implements SqlConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final String value) {
        builder.or(cond -> cond
                .property(property).lt(toJavaType(value))
                .property(property).eq(toJavaType(value))
        );
    }
}
