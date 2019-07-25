package net.ninjacat.objmatcher.matcher;

import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;

@FunctionalInterface
public interface ObjectMatcher<T> {
    boolean matches(T object, ObjectPattern pattern);
}
