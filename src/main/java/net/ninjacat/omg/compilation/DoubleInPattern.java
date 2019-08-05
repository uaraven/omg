package net.ninjacat.omg.compilation;

import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class DoubleInPattern<T> extends BaseInPattern<T, Double> {

    DoubleInPattern(final Property<T> property, final List<Double> matchingValue) {
        super(property, matchingValue);
    }

    @Override
    Class<? extends Double> getType() {
        return Double.class;
    }
}
