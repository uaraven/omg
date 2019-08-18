package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import net.ninjacat.omg.sql.parser.OmSqlLexer;
import net.ninjacat.omg.sql.parser.OmSqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;

import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class SqlParser {
    private final OmSqlParser.WhereContext where;

    public SqlParser(final String filter) {
        final OmSqlLexer lexer = new OmSqlLexer(CharStreams.fromString(filter));
        final OmSqlParser parser = new OmSqlParser(new CommonTokenStream(lexer));
        final OmSqlParser.FilterContext tree = parser.filter();
        this.where = tree.sql_stmt().select().where();
    }

    public Condition getCondition() {
        final Conditions.LogicalConditionBuilder builder = Conditions.matcher();

        final OmSqlParser.ExprContext expr = where.expr();
        processExpression(expr, builder);

        return builder.build();
    }

    private void processExpression(final OmSqlParser.ExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        Match(expr).of(
                Case($(instanceOf(OmSqlParser.ConditionContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.AndExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.OrExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.NotExprContext.class)), ctx -> run(() -> processExpression(ctx, builder)))
        );
    }

    private void processExpression(final OmSqlParser.AndExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.and(cond -> {
                    processExpression(expr.expr(0), cond);
                    processExpression(expr.expr(1), cond);
                }
        );
    }

    private void processExpression(final OmSqlParser.OrExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.or(cond -> {
                    processExpression(expr.expr(0), cond);
                    processExpression(expr.expr(1), cond);
                }
        );
    }

    private void processExpression(final OmSqlParser.NotExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.not(cond -> {
                    processExpression(expr.expr(), cond);
                }
        );
    }

    private void processExpression(final OmSqlParser.ConditionContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String operation = Optional.ofNullable(expr.operator()).map(RuleContext::getText).orElse(null);
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);
        final String value = expr.literal_value().getText();
        final Operation conditionOperation = Operation.byOpCode(operation)
                .getOrElseThrow(() -> new SqlParsingException("Unsupported operation '%s'", operation));
        conditionOperation.getProducer().create(builder, property, value);
    }
}
