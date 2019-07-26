package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

@Value
public class IntegerGt implements Matcher<Long> {
    long value;

    @Override
    public boolean matches(final Long checkedValue) {
        return checkedValue.compareTo(getValue()) > 0;
    }

    @Override
    public String toString() {
        return "> " + value;
    }
}
