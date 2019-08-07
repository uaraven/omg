package net.ninjacat.omg.reflect;

import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class EnumInPattern<T> extends BaseInPattern<T, Enum> {

    EnumInPattern(final Property<T> property, final List<Enum> matchingValue) {
        super(property, matchingValue);
    }

    @Override
    Class<? extends Enum> getType() {
        return Enum.class;
    }

}
