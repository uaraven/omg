package net.ninjacat.omg.reflection;

import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class LongInPattern<T> extends BaseInPattern<T, Long> {

    LongInPattern(final Property<T> property, final List<Long> matchingValue) {
        super(property, matchingValue);
    }

    @Override
    Class<? extends Long> getType() {
        return Long.class;
    }
}
