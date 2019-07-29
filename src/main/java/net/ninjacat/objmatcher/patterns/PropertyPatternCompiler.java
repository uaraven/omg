package net.ninjacat.objmatcher.patterns;

import net.ninjacat.objmatcher.conditions.PropertyCondition;

/**
 * Creates a {@link PropertyPattern} from a property condition.
 *
 * @param <T> Class containing property
 */
public interface PropertyPatternCompiler<T> {
    <P> PropertyPattern<T> build(PropertyCondition<P> condition);
}
