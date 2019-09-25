package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.omql.parser.OmqlParser;

/**
 * Producer for SQL condition IN (subquery)
 */
public class MatchProducer implements OmqlConditionProducer<OmqlParser.MatchExprContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final QueryContext context,
                       final OmqlParser.MatchExprContext value) {
        final QueryCompiler parser = QueryCompiler.ofParsed(value.select(), context.allowedSources());
        builder.property(property).match(parser.getCondition());
    }
}
