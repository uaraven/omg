package net.ninjacat.omg.json;

import net.ninjacat.omg.conditions.Conditions;

@FunctionalInterface
public interface PropertyConditionProducer {
    void create(Conditions.LogicalConditionBuilder builder, String property, Object value);
}
