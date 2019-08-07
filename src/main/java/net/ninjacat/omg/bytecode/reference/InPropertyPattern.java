package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.BasePropertyPattern;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.TypeConversionException;

import java.util.Collections;
import java.util.List;

public abstract class InPropertyPattern<T, E> extends BasePropertyPattern<T> {
    private final List<E> matchingValue;

    protected InPropertyPattern(final Property property, final T matchingValue) {
        super(property);
        this.matchingValue = getMatchingValueConverted(matchingValue);
    }

    private List<E> getMatchingValueConverted(final T mv) {
        if (mv == null) {
            return Collections.emptyList();
        } else if (mv instanceof List) {
            return Collections.unmodifiableList((List<E>)mv);
        } else {
            throw new TypeConversionException(mv.getClass(), mv, List.class);
        }
    }

    public List<E> getMatchingValue() {
        return matchingValue;
    }

    public boolean isInList(final E propertyValue, final List<E> matchingValue) {
        return matchingValue.stream().anyMatch(item -> item.equals(propertyValue));
    }
}
