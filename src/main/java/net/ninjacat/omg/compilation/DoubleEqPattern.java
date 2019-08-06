package net.ninjacat.omg.compilation;

import net.jcip.annotations.Immutable;

@Immutable
public class DoubleEqPattern<T> extends BaseDoublePattern<T> {

    DoubleEqPattern(final Property property, final double matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final double propertyValue) {
        return propertyValue == getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
