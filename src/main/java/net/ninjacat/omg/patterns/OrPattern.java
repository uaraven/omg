package net.ninjacat.omg.patterns;

import java.util.List;
import java.util.stream.Collectors;

public class OrPattern<T> extends GroupPattern<T> {

    OrPattern(final List<Pattern<T>> patterns) {
        super(patterns);
    }

    @Override
    public boolean matches(final T instance) {
        return getPatterns().anyMatch(it -> it.matches(instance));
    }

    @Override
    public String toString() {
        return "Or{" + getPatterns().map(Object::toString).collect(Collectors.joining(", ")) + "}";
    }

}
