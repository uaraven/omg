package net.ninjacat.omg.reflect;

public class EnumEqPattern<T> extends BaseEnumPattern<T> {
    EnumEqPattern(final Property property, final Enum matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected boolean compare(final Enum propertyValue, final Enum matchingValue) {
        return propertyValue == matchingValue;
    }

    @Override
    protected String getComparatorAsString() {
        return "==";
    }
}
