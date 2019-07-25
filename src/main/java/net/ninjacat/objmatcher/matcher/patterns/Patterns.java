package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

public final class Patterns {
    private Patterns() {
    }

    public static StringPatternBuilder string(final String fieldName) {
        return new StringPatternBuilder(fieldName);
    }

    @Value
    public static class StringPatternBuilder {
        String fieldName;

        public StringPatternBuilder(final String fieldName) {
            this.fieldName = fieldName;
        }

        public StringEquals equalTo(final String value) {
            return new StringEquals(fieldName, value);
        }

        public StringNotEqual notEqualTo(final String value) {
            return new StringNotEqual(fieldName, value);
        }
    }
}
