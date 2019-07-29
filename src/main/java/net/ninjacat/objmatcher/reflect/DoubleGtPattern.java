package net.ninjacat.objmatcher.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class DoubleGtPattern<T> extends BaseDoublePattern<T> {

    DoubleGtPattern(final Property property, final double matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final double propertyValue) {
        return propertyValue > getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return ">";
    }
}
