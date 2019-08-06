package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.JsonParsingException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InProducer implements PropertyConditionProducer {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder, final String property, final JsonNode value) {
        Assert.isArray(value);
        final ArrayNode array = (ArrayNode) value;
        Assert.noEmpty(array);
        final List<Object> items = StreamSupport.stream(array.spliterator(), false).map(this::toJavaType).collect(Collectors.toList());
        final Class firstItemClass = items.get(0).getClass();
        final boolean allSameClass = items.stream().skip(1).allMatch(it -> it.getClass().equals(firstItemClass));
        if (!allSameClass) {
            throw new JsonParsingException("All elements of array must be of same type. Actual: '%s'", items);
        }
        builder.property(property).in(items);
    }
}
