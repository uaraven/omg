package net.ninjacat.objmatcher.matcher.reflect;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class LongEqPattern<T> extends BaseLongPattern<T> {

    LongEqPattern(final Property property, final long matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final long propertyValue, final long matchingValue) {
        return propertyValue == matchingValue;
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
