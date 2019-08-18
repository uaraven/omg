package net.ninjacat.omg.sql;

import com.fasterxml.jackson.databind.JsonNode;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.json.ConditionParser;

import static io.vavr.API.*;

@FunctionalInterface
public interface SqlConditionProducer {
    void create(Conditions.LogicalConditionBuilder builder, String property, String value);

    default Object toJavaType(final JsonNode node) {
        return Match(node).of(
                Case($(JsonNode::isLong), JsonNode::asLong),
                Case($(JsonNode::isTextual), JsonNode::asText),
                Case($(JsonNode::isIntegralNumber), JsonNode::asInt),
                Case($(JsonNode::isDouble), JsonNode::asDouble),
                Case($(JsonNode::isObject), ConditionParser::parseTree)
        );
    }
}
