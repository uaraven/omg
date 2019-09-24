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
                       final TypeValidator validator,
                       final OmqlParser.MatchExprContext value) {
        AntlrTools.assertError(value.select().children);
        final QueryCompiler parser = QueryCompiler.ofParsed(value.select(), allowedSources);
        builder.property(property).match(parser.getCondition());
    }
}
