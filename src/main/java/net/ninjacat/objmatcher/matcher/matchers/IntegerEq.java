package net.ninjacat.objmatcher.matcher.matchers;

import lombok.Value;

@Value
public class IntegerEq implements IntMatcher {
    long value;

    @Override
    public boolean matches(final Long fieldValue) {
        return Long.compare(fieldValue, getValue()) == 0;
    }

    @Override
    public String toString() {
        return "== "+ value;
    }
}
