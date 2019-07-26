package net.ninjacat.objmatcher.matcher.matchers;

import lombok.Value;

@Value
public class StringNotEq implements StringMatcher {

    String value;

    @Override
    public boolean matches(final String checkedValue) {
        return !getValue().equals(checkedValue);
    }

    @Override
    public String toString() {
        return "!= '" + value + '\'';
    }
}
