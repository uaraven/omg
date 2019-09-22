package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

@FunctionalInterface
public interface SqlConditionProducer<T extends OmSqlParser.ExprContext> {
    void create(Conditions.LogicalConditionBuilder builder, String property, TypeValidator validator, T value);


}
