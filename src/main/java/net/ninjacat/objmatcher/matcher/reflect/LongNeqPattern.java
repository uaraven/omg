package net.ninjacat.objmatcher.matcher.reflect;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class LongNeqPattern<T> extends BaseLongPattern<T> {

    LongNeqPattern(final Property property, final long matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final long propertyValue, final long matchingValue) {
        return propertyValue != matchingValue;
    }

    @Override
    protected String getComparatorAsString() {
        return "!=";
    }
}
