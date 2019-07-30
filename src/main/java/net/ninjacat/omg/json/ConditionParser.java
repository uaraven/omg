package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.control.Try;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.JsonParsingException;

import java.util.Optional;

public final class ConditionParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ConditionParser() {
    }

    public static Condition parse(final String json) {
        final Conditions.LogicalConditionBuilder builder = Conditions.start();
        final JsonNode root = Try.of(() -> OBJECT_MAPPER.readTree(json))
                .getOrElseThrow((ex) -> new JsonParsingException(ex, "Failed to parse JSON"));
        if (root.isArray()) {
             parseAnd(builder, (ArrayNode) root);
        } else {
             parsePropertyNode(builder, (ObjectNode) root);
        }
        return builder.build();
    }

    private static void parsePropertyNode(final Conditions.LogicalConditionBuilder builder, final ObjectNode root) {
        final String operation = Optional.ofNullable(root.get("op")).map(Object::toString).orElse(null);
        final String property = Optional.ofNullable(root.get("property")).map(Object::toString).orElse(null);
        final Object value = root.get("value");
        if (operation == null) {
            throw new JsonParsingException("Property 'op' is missing in\n%s", root.toString());
        }
        if (property == null) {
            throw new JsonParsingException("Property 'property' is missing in\n%s", root.toString());
        }
        if (value == null) {
            throw new JsonParsingException("Property 'value' is missing in\n%s", root.toString());
        }
        final Operation conditionOperation = Operation.byOpCode(operation)
                .getOrElseThrow(() -> new JsonParsingException("Unsupported operation '%s'", operation));
        conditionOperation.getProducer().create(builder, property, value);
    }

    private static void parseAnd(final Conditions.LogicalConditionBuilder builder, final ArrayNode root) {

    }
}
