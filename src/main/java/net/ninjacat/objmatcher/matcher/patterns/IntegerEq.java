package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

@Value
public class IntegerEq implements Matcher<Long> {
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
