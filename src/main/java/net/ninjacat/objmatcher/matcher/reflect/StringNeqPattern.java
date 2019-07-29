package net.ninjacat.objmatcher.matcher.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class StringNeqPattern<T> extends BaseStringPattern<T> {

    StringNeqPattern(final Property property, final String matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final String propertyValue) {
        if (propertyValue == null || getMatchingValue() == null && propertyValue != getMatchingValue()) {
            return true;
        } else {
            return !propertyValue.equals(getMatchingValue());
        }
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
