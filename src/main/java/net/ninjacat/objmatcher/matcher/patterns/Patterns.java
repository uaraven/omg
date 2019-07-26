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

        public StringEq equalTo(final String value) {
            return new StringEq(fieldName, value);
        }

        public StringNotEq notEqualTo(final String value) {
            return new StringNotEq(fieldName, value);
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

        public IntegerEq equalTo(final long value) {
            return new IntegerEq(fieldName, value);
        }

        public IntegerNotEq notEqualTo(final long value) {
            return new IntegerNotEq(fieldName, value);
        }

        public IntegerLt lessThan(final long value) {
            return new IntegerLt(fieldName, value);
        }

        public IntegerGt greaterThan(final long value) {
            return new IntegerGt(fieldName, value);
        }
    }
}
