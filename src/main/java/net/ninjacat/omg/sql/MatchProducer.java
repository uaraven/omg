package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlParser;

/**
 * Producer for SQL condition IN (subquery)
 */
public class MatchProducer implements SqlConditionProducer<OmSqlParser.MatchExprContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmSqlParser.MatchExprContext value) {
        AntlrTools.assertError(value.select().children);
        final SqlParser parser = SqlParser.ofParsed(value.select());
        builder.property(property).match(parser.getCondition());
    }
}
