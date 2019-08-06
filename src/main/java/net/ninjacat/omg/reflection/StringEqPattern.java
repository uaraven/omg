package net.ninjacat.omg.reflection;


import net.jcip.annotations.Immutable;

@Immutable
public class StringEqPattern<T> extends BaseStringPattern<T> {

    StringEqPattern(final Property property, final String matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final String propertyValue) {
        if (propertyValue == null && getMatchingValue() == null) {
            return true;
        } else {
            return propertyValue != null && propertyValue.equals(getMatchingValue());
        }
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
