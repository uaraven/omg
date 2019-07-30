package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.reflect.Property;

public class TestPattern<T> extends BasePropertyPattern<T ,Integer> {

    public TestPattern(Property property, Integer matchingValue) {
        super(property, matchingValue);
    }

    @Override
    protected Integer getPropertyValue(T instance) {
        return null;
    }
}
