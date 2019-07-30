package net.ninjacat.omg.patterns;

import net.ninjacat.omg.conditions.PropertyCondition;

/**
 * Creates a {@link PropertyPattern} from a property condition.
 *
 * @param <T> Class containing property
 */
public interface PropertyPatternCompiler<T> {
    <P> PropertyPattern<T> build(PropertyCondition<P> condition);
}
