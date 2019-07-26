package net.ninjacat.objmatcher.matcher.matchers;

@FunctionalInterface
public interface Matcher<T> {
    boolean matches(final T checkedValue);
}
