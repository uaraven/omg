package net.ninjacat.objmatcher.matcher.matchers;

import lombok.Value;

@Value
public class IntegerLt implements IntMatcher {
    long value;

    @Override
    public boolean matches(final Long checkedValue) {
        return checkedValue.compareTo(getValue()) < 0;
    }

    @Override
    public String toString() {
        return "< " + value;
    }
}
