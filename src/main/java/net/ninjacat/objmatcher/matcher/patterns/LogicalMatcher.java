package net.ninjacat.objmatcher.matcher.patterns;


import java.util.List;

/**
 * Logical match allows to combine multiple matchers for one property.
 *
 * @param <T>
 */
public abstract class LogicalMatcher<T> implements Matcher<T> {
    private final List<? extends Matcher<T>> childMatchers;

    protected LogicalMatcher(final List<? extends Matcher<T>> matchers) {
        childMatchers = io.vavr.collection.List.ofAll(matchers).asJava();
    }

    public List<? extends Matcher<T>> getChildMatchers() {
        return childMatchers;
    }
}
