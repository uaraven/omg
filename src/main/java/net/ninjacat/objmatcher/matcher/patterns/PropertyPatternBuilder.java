package net.ninjacat.objmatcher.matcher.patterns;

import net.ninjacat.objmatcher.matcher.conditions.PropertyCondition;

public interface PropertyPatternBuilder<T> {
    <P> PropertyPattern<T> build(PropertyCondition<P> condition);
}
