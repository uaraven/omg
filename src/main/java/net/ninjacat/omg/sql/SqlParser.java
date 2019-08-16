package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.sql.parser.OmSqlLexer;
import net.ninjacat.omg.sql.parser.OmSqlParser;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenStream;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class SqlParser {
    private final OmSqlParser.SelectContext select;
    private final OmSqlParser.WhereContext where;

    public SqlParser(final String filter) {
        final OmSqlLexer lexer = new OmSqlLexer(CharStreams.fromString(filter));
        final TokenStream tokenStream = new BufferedTokenStream(lexer);
        final OmSqlParser parser = new OmSqlParser(tokenStream);
        this.select = parser.filter().sql_stmt().select();
        this.where = parser.filter().sql_stmt().select().where();
    }

    public Condition getCondition() {
        final Conditions.ConditionBuilder builder = Conditions.matcher();

        final OmSqlParser.ExprContext expr = where.expr();
        processExpression(expr, builder);

        return builder.build();
    }

    private void processExpression(final OmSqlParser.ExprContext expr, final Conditions.ConditionBuilder builder) {
        Match(expr).of(
                Case($(instanceOf(OmSqlParser.AndExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.OrExprContext.class)), ctx -> run(() -> processExpression(ctx, builder)))
        );
    }

    private void processExpression(final OmSqlParser.AndExprContext expr, final Conditions.ConditionBuilder builder) {

    }

    private void processExpression(final OmSqlParser.OrExprContext expr, final Conditions.ConditionBuilder builder) {
    }
}
