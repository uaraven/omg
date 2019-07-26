package net.ninjacat.objmatcher.matcher.patterns;

import java.util.List;

public class LogicalPattern<T> implements Pattern<List<T>> {
    @Override
    public boolean matches(final List<T> checkedValue) {
        return false;
    }
}
