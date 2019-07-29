package net.ninjacat.objmatcher.reflect;

import net.jcip.annotations.Immutable;

@Immutable
public class LongGtPattern<T> extends BaseLongPattern<T> {

    LongGtPattern(final Property property, final long matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final long propertyValue) {
        return propertyValue > getMatchingValue();
    }

    @Override
    protected String getComparatorAsString() {
        return ">";
    }
}
