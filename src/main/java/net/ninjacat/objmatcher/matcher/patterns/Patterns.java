package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

public final class Patterns {
    private Patterns() {
    }

    public static StringPatternBuilder string(final String fieldName) {
        return new StringPatternBuilder(fieldName);
    }

    public static IntegerPatternBuilder integer(final String fieldName) {
        return new IntegerPatternBuilder(fieldName);
    }

    @Value
    public static class StringPatternBuilder {
        String fieldName;

        StringPatternBuilder(final String fieldName) {
            this.fieldName = fieldName;
        }

        public StringEquals equalTo(final String value) {
            return new StringEquals(fieldName, value);
        }

        public StringNotEqual notEqualTo(final String value) {
            return new StringNotEqual(fieldName, value);
        }

        public StringRegex matches(final String pattern) {
            return new StringRegex(fieldName, pattern);
        }
    }

    public static class IntegerPatternBuilder {
        private final String fieldName;

        IntegerPatternBuilder(final String fieldName) {
            this.fieldName = fieldName;
        }

        public IntegerEq equalTo(final int value) {
            return new IntegerEq(fieldName, value);
        }

        public IntegerNotEq notEqualTo(final int value) {
            return new IntegerNotEq(fieldName, value);
        }

        public IntegerLt lessThan(final int value) {
            return new IntegerLt(fieldName, value);
        }

        public IntegerGt greaterThan(final int value) {
            return new IntegerGt(fieldName, value);
        }
    }
}
