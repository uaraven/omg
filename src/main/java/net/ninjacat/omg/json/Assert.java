package net.ninjacat.omg.json;

import com.fasterxml.jackson.databind.JsonNode;
import net.ninjacat.omg.errors.JsonParsingException;

final class Assert {
    private Assert() {
    }
    
    static void isValue(final JsonNode node) {
        if (!node.isValueNode()) {
            throw new JsonParsingException("Value expected, but received %s", node);
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
}
