package net.ninjacat.omg.reflect;


import net.jcip.annotations.Immutable;

@Immutable
public class ObjectEqPattern<T> extends BaseObjectPattern<T> {

    ObjectEqPattern(final Property property, final Object matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected String getOperator() {
        return "==";
    }

    @Override
    public boolean matches(final T instance) {
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue == null && getMatchingValue() == null) {
            return true;
        } else {
            return propertyValue != null && propertyValue.equals(getMatchingValue());
        }
    }

}
