package net.ninjacat.objmatcher.matcher.patterns;

public interface Pattern<T> {
    public abstract boolean matches(final T checkedValue);
}
