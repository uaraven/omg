package net.ninjacat.omg.compilation;

import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class StringInPattern<T> extends BaseInPattern<T, String> {

    StringInPattern(final Property<T> property, final List<String> matchingValue) {
        super(property, matchingValue);
    }

    @Override
    Class<? extends String> getType() {
        return String.class;
    }

}
