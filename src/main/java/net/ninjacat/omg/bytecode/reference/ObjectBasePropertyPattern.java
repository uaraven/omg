package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;

public abstract class ObjectBasePropertyPattern<T> extends BasePropertyPattern<T> {
    private final Object matchingValue;

    protected ObjectBasePropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = matchingValue;
    }

    public Object getMatchingValue() {
        return matchingValue;
    }

}
