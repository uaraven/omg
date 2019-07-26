package net.ninjacat.objmatcher.matcher.matchers;

import lombok.Value;
import net.ninjacat.objmatcher.matcher.reflect.PropertyMatcher;

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

    public static LogicalAndBuilder and() {
        return new LogicalAndBuilder();
    }

    public static LogicalAndBuilder or() {
        return new LogicalAndBuilder();
    }

    @Value
    public static class StringPatternBuilder {
        String fieldName;

        public PropertyMatcher<String> equalTo(final String value) {
            return new PropertyMatcher<>(fieldName, new StringEq(value));
        }

        public PropertyMatcher<String> notEqualTo(final String value) {
            return new PropertyMatcher<>(fieldName, new StringNotEq(value));
        }

        public PropertyMatcher<String> matches(final String pattern) {
            return new PropertyMatcher<>(fieldName, new StringRegex(pattern));
        }
    }

    @Value
    public static class IntegerPatternBuilder {
        String fieldName;

        public PropertyMatcher<Long> equalTo(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerEq(value));
        }

        public PropertyMatcher<Long> notEqualTo(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerNotEq(value));
        }

        public PropertyMatcher<Long> lessThan(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerLt(value));
        }

        public PropertyMatcher<Long> greaterThan(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerGt(value));
        }
    }

    public static class LogicalPatternBuilder<T> {
        private final List<Matcher<T>> matchers = new ArrayList<>();

        public void match(final Matcher<T> matcher) {
            matchers.add(matcher);
        }

        List<Matcher<T>> getMatchers() {
            return io.vavr.collection.List.ofAll(matchers).asJava();
        }
    }

    public static final class LogicalAndBuilder<T> extends LogicalPatternBuilder<T> {

        public LogicalAndMatcher<T> done() {
            return new LogicalAndMatcher<>(getMatchers());
        }

    }

    public static final class LogicalOrBuilder<T> extends LogicalPatternBuilder<T> {
        public LogicalOrMatcher<T> done() {
            return new LogicalOrMatcher<T>(getMatchers());
        }

    }
}
