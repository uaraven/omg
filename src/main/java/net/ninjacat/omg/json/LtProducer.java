package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import net.ninjacat.omg.conditions.Conditions;

public class LtProducer implements PropertyConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final JsonNode value) {
        Assert.isValue(value);
        builder.property(property).lt(toJavaType(value));
    }
}
