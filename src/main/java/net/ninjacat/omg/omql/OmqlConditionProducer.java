package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.omql.parser.OmqlParser;

@FunctionalInterface
public interface OmqlConditionProducer<T extends OmqlParser.ExprContext> {
    void create(Conditions.LogicalConditionBuilder builder, String property, TypeValidator validator, T value);


}
