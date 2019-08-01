package net.ninjacat.omg.patterns;

import java.util.List;
import java.util.stream.Collectors;

public class AndPattern<T> extends GroupPattern<T> {

    AndPattern(final List<Pattern<T>> patterns) {
        super(patterns);
    }

    @Override
    public boolean matches(final T instance) {
        return getPatterns().allMatch(it -> it.matches(instance));
    }

    @Override
    public String toString() {
        return "And{" + getPatterns().map(Object::toString).collect(Collectors.joining(", ")) + "}";
    }
}
