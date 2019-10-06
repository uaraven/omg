package net.ninjacat.omg.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class BooleanEqPattern<T> extends BaseBooleanPattern<T> {

    BooleanEqPattern(final Property property, final boolean matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final boolean propertyValue) {
        return propertyValue == getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
