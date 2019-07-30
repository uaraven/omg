package net.ninjacat.omg.reflect;

public class EnumNeqPattern<T> extends BaseEnumPattern<T> {
    EnumNeqPattern(final Property property, final String matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final Enum propertyValue, final Enum matchingValue) {
        return propertyValue != matchingValue;
    }

    @Override
    protected String getComparatorAsString() {
        return "!=";
    }
}