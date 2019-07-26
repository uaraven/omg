package net.ninjacat.objmatcher.matcher.matchers;


import net.jcip.annotations.Immutable;

import java.util.List;

/**
 * Logical match allows to combine multiple matchers for one property.
 *
 * @param <T>
 */
@Immutable
public abstract class LogicalMatcher<T> implements Matcher<T> {
    private final io.vavr.collection.List<? extends Matcher<T>> childMatchers;

    protected LogicalMatcher(final List<? extends Matcher<T>> matchers) {
        childMatchers = io.vavr.collection.List.ofAll(matchers);
    }

    public List<? extends Matcher<T>> getChildMatchers() {
        return childMatchers.asJava();
    }
}
