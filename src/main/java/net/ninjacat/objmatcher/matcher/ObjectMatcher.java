package net.ninjacat.objmatcher.matcher;

import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;

public interface ObjectMatcher<T> {
    boolean matches(T object, ObjectPattern pattern);
}
