package net.ninjacat.omg.patterns;

public class NotPattern<T> implements Pattern<T> {

    private final Pattern<T> childPattern;

    public NotPattern(final Pattern<T> childPattern) {
        this.childPattern = childPattern;
    }

    @Override
    public boolean matches(final T instance) {
        return !childPattern.matches(instance);
    }
}
