package net.ninjacat.omg.bytecode.primitive;

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
            return Collections.unmodifiableList((List<E>) mv);
        } else {
            throw new TypeConversionException(mv.getClass(), mv, List.class);
        }
    }

    public List<E> getMatchingValue() {
        return matchingValue;
    }

    /**
     * <strong>Note:</strong> The order of parameters is different from similar method in "reference" package
     * because of the order of parameters on stack
     *
     * @param matchingValue
     * @param propertyValue
     * @return
     */
    public boolean isInList(final List<E> matchingValue, final E propertyValue) {
        return matchingValue.stream().anyMatch(item -> item.equals(propertyValue));
    }
}
