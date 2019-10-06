package net.ninjacat.omg.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class BooleanNeqPattern<T> extends BaseBooleanPattern<T> {

    BooleanNeqPattern(final Property property, final boolean matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final boolean propertyValue) {
        return propertyValue != getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return "!=";
    }
}
