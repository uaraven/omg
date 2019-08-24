package net.ninjacat.omg.reflect;


import net.jcip.annotations.Immutable;

@Immutable
public class ObjectNeqPattern<T> extends BaseObjectPattern<T> {

    ObjectNeqPattern(final Property property, final Object matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected String getOperator() {
        return "!=";
    }

    @Override
    public boolean matches(final T instance) {
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue == null && getMatchingValue() == null) {
            return false;
        } else if (propertyValue == null || getMatchingValue() == null) {
            return true;
        } else {
            return !propertyValue.equals(getMatchingValue());
        }
    }
}
