package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

public final class Matchers {
    private Matchers() {
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

        public FieldPattern<String> equalTo(final String value) {
            return new FieldPattern<>(fieldName, String.class, new StringEq(value));
        }

        public FieldPattern<String> notEqualTo(final String value) {
            return new FieldPattern<>(fieldName, String.class, new StringNotEq(value));
        }

        public FieldPattern<String> matches(final String pattern) {
            return new FieldPattern<>(fieldName, String.class, new StringRegex(pattern));
        }

        public <T> LogicalAndBuilder<T> and() {
            return new LogicalAndBuilder<T>(fieldName, String.class);
        }

        public <T> LogicalOrBuilder<T> or() {
            return new LogicalOrBuilder<T>(fieldName, String.class);
        }
    }

    @Value
    public static class IntegerPatternBuilder {
        String fieldName;

        public FieldPattern<Long> equalTo(final long value) {
            return new FieldPattern<>(fieldName, Long.class, new IntegerEq(value));
        }

        public FieldPattern<Long> notEqualTo(final long value) {
            return new FieldPattern<>(fieldName, Long.class, new IntegerNotEq(value));
        }

        public FieldPattern<Long> lessThan(final long value) {
            return new FieldPattern<>(fieldName, Long.class, new IntegerLt(value));
        }

        public FieldPattern<Long> greaterThan(final long value) {
            return new FieldPattern<>(fieldName, Long.class, new IntegerGt(value));
        }

        public  <T> LogicalAndBuilder<T> and() {
            return new LogicalAndBuilder<T>(fieldName, Long.class);
        }

        public <T> LogicalOrBuilder<T> or(final Matcher<T> matcher) {
            final LogicalOrBuilder<T> builder = new LogicalOrBuilder<T>(fieldName, Long.class);
            builder.or(matcher);
            return builder;
        }

    }

    public static class LogicalPatternBuilder<T> {
        private final String fieldName;
        private final Class fieldType;
        private final List<Matcher<T>> matchers = new ArrayList<>();

        private LogicalPatternBuilder(final String fieldName, final Class fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        void addMatcher(final Matcher<T> matcher) {
            matchers.add(matcher);
        }

        String getFieldName() {
            return fieldName;
        }

        Class getFieldType() {
            return fieldType;
        }

        List<Matcher<T>> getMatchers() {
            return io.vavr.collection.List.ofAll(matchers).asJava();
        }
    }

    public static final class LogicalAndBuilder<T> extends LogicalPatternBuilder<T> {
        private LogicalAndBuilder(final String fieldName, final Class fieldType) {
            super(fieldName, fieldType);
        }

        public void and(final Matcher<T> matcher) {
            addMatcher(matcher);
        }

        public FieldPattern<T> done() {
            return new FieldPattern<T>(getFieldName(), getFieldType(), new LogicalAndMatcher<>(getMatchers()));
        }

    }

    public static final class LogicalOrBuilder<T> extends LogicalPatternBuilder<T> {
        private LogicalOrBuilder(final String fieldName, final Class fieldType) {
            super(fieldName, fieldType);
        }

        public void or(final Matcher<T> matcher) {
            addMatcher(matcher);
        }

        public FieldPattern<T> done() {
            return new FieldPattern<T>(getFieldName(), getFieldType(), new LogicalOrMatcher<>(getMatchers()));
        }

    }
}
