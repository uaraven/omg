package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.ObjectCondition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import net.ninjacat.omg.patterns.PropertyPattern;


/**
 * Base class for regex matching property value.
 * @param <T>
 */
public abstract class BaseObjectMatchPropertyPattern<T> implements PropertyPattern<T> {
    private final Property property;
    private final Pattern matchingPattern;

    protected BaseObjectMatchPropertyPattern(final Property property, final Object condition) {
        this.property = property;
        this.matchingPattern = Patterns.compile(
                (Condition) condition,
                AsmPatternCompiler.forClass(property.getType()));
    }

    public Property getProperty() {
        return property;
    }

    public Pattern getMatchingValue() {
        return matchingPattern;
    }

    @Override
    public abstract boolean matches(final T instance);

}
