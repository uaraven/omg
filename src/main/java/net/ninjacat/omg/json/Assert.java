package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ninjacat.omg.errors.JsonParsingException;

import java.util.List;

final class Assert {
    private Assert() {
    }
    
    static void isValue(final JsonNode node) {
        if (!node.isValueNode()) {
            throw new JsonParsingException("Value expected, but received %s", node);
        }
    }

    static void isArray(final JsonNode node) {
        if (!node.isArray()) {
            throw new JsonParsingException("Array expected, but received %s", node);
        }
    }

    static void isNotValue(final JsonNode node) {
        if (node.isValueNode()) {
            throw new JsonParsingException("Array or Object expected, but received %s", node);
        }
    }

    static void isText(final JsonNode node) {
        if (!node.isTextual()) {
            throw new JsonParsingException("Text expected, but received %s", node);
        }
    }

    static void noEmpty(final ArrayNode array) {
        if (array.size() == 0) {
            throw new JsonParsingException("Array should not be empty");
        }
    }
}
