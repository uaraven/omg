package net.ninjacat.objmatcher.matcher.matchers;

public interface TypedMatcher<T> extends Matcher<T> {
    Class getExpectedType();
}
