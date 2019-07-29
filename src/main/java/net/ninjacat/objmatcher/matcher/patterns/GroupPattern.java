package net.ninjacat.objmatcher.matcher.patterns;

import java.util.List;
import java.util.stream.Stream;

public abstract class GroupPattern<T> implements Pattern<T> {
    private final List<Pattern<T>> patterns;

    public GroupPattern(final List<Pattern<T>> patterns) {
        this.patterns = io.vavr.collection.List.ofAll(patterns).asJava();
    }

    public Stream<Pattern<T>> getPatterns() {
        return patterns.stream();
    }
}
