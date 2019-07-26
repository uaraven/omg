package net.ninjacat.objmatcher.matcher.patterns;

@FunctionalInterface
public interface Matcher<T> {
    boolean matches(final T checkedValue);
}
