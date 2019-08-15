package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.control.Try;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.JsonParsingException;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class ConditionParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ConditionParser() {
    }

    public static Condition parse(final String json) {
        final JsonNode root = Try.of(() -> OBJECT_MAPPER.readTree(json))
                .getOrElseThrow((ex) -> new JsonParsingException(ex, "Failed to parse JSON"));

        return new ConditionParser().parseJson(root);
    }

    public static Condition parseTree(final JsonNode root) {
        return new ConditionParser().parseJson(root);
    }

    private Condition parseJson(final JsonNode root) {
        final Conditions.LogicalConditionBuilder builder = Conditions.matcher();
        if (root.isArray()) {
            parseAnd(builder, (ArrayNode) root);
        } else {
            parseNode(builder, (ObjectNode) root);
        }
        return builder.build();
    }

    private void parseNode(final Conditions.LogicalConditionBuilder builder, final ObjectNode node) {
        final String operation = Optional.ofNullable(node.get("op")).map(JsonNode::asText).orElseThrow(
                () -> new JsonParsingException("Invalid JSON, op' property is required in '%s'", node)
        );
        if (isLogical(operation)) {
            parseLogicalNode(builder, operation, node);
        } else {
            parsePropertyNode(builder, node);
        }
    }

    private void parseLogicalNode(final Conditions.LogicalConditionBuilder builder, final String operation, final ObjectNode node) {
        final JsonNode value = node.get("value");

        switch (operation.toLowerCase(Locale.US)) {
            case "and":
                ensureArray(value, operation);
                parseAnd(builder, (ArrayNode) value);
                break;
            case "or":
                ensureArray(value, operation);
                parseOr(builder, (ArrayNode) value);
                break;
            case "not":
                ensureObject(value, operation);
                parseNot(builder, (ObjectNode) value);
        }
    }

    private void ensureArray(final JsonNode value, final String operation) {
        if (!value.isArray()) {
            throw new JsonParsingException("'value' must be an array in '%s' object", operation);
        }
    }

    private void ensureObject(final JsonNode value, final String operation) {
        if (!value.isObject()) {
            throw new JsonParsingException("'value' must be an object in '%s' condition", operation);
        }
    }

    private boolean isLogical(final String operation) {
        return operation.equalsIgnoreCase("and") || operation.equalsIgnoreCase("or") ||
                operation.equalsIgnoreCase("not");
    }

    private void parsePropertyNode(final Conditions.LogicalConditionBuilder builder, final ObjectNode root) {
        final String operation = Optional.ofNullable(root.get("op")).map(JsonNode::asText).orElse(null);
        final String property = Optional.ofNullable(root.get("property")).map(JsonNode::asText).orElse(null);
        final JsonNode value = root.get("value");
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

    private void parseAnd(final Conditions.LogicalConditionBuilder builder, final ArrayNode root) {
        builder.and(cond ->
                StreamSupport.stream(root.spliterator(), false)
                        .filter(JsonNode::isObject)
                        .map(node -> (ObjectNode) node)
                        .forEach(node -> parseNode(cond, node))
        );
    }


    private void parseNot(final Conditions.LogicalConditionBuilder builder, final ObjectNode node) {
        builder.not(cond -> parseNode(cond, node));
    }

    private void parseOr(final Conditions.LogicalConditionBuilder builder, final ArrayNode root) {
        builder.or(cond ->
                StreamSupport.stream(root.spliterator(), false)
                        .filter(JsonNode::isObject)
                        .map(node -> (ObjectNode) node)
                        .forEach(node -> parseNode(cond, node))
        );
    }
}
