package net.ninjacat.omg.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class LongEqPattern<T> extends BaseLongPattern<T> {

    LongEqPattern(final Property property, final long matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final long propertyValue) {
        return propertyValue == getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
