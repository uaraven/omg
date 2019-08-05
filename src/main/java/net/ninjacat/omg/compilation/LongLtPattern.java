package net.ninjacat.omg.compilation;

import net.jcip.annotations.Immutable;

@Immutable
public class LongLtPattern<T> extends BaseLongPattern<T> {

    LongLtPattern(final Property property, final long matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final long propertyValue) {
        return propertyValue < getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return "<";
    }
}
