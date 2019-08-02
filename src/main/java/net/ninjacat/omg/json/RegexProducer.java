package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import net.ninjacat.omg.conditions.Conditions;

public class RegexProducer implements PropertyConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final JsonNode value) {
        Assert.isText(value);
        builder.property(property).regex(value.textValue());
    }
}
