package net.ninjacat.omg.compilation;

import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class ObjectInPattern<T> extends BaseInPattern<T, Object> {

    ObjectInPattern(final Property<T> property, final List<Object> matchingValue) {
        super(property, matchingValue);
    }

    @Override
    Class<? extends Object> getType() {
        return Object.class;
    }

}
