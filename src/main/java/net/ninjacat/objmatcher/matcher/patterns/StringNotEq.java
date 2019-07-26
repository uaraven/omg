package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

@Value
public class StringNotEq implements Matcher<String> {

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
