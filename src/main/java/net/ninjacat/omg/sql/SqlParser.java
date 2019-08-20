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

    public static SqlParser of(final String query) {
        final OmSqlLexer lexer = new OmSqlLexer(CharStreams.fromString(query));
        final OmSqlParser parser = new OmSqlParser(new CommonTokenStream(lexer));
        final OmSqlParser.FilterContext tree = parser.filter();

        return new SqlParser(tree.sql_stmt().select().where());
    }

    static SqlParser ofParsed(OmSqlParser.SelectContext select) {
        return new SqlParser(select.where());
    }

    private SqlParser(final OmSqlParser.WhereContext where) {
        this.where = where;
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
                Case($(instanceOf(OmSqlParser.NotExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.InExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmSqlParser.MatchExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(), () -> {
                    throw new SqlParsingException("Unsupported expression: %s", expr.getText());
                })
        );
    }

    private void processExpression(final OmSqlParser.InExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);

        final Operation conditionOperation = Operation.byOpCode("in")
                .getOrElseThrow(() -> new SqlParsingException("Unsupported operation 'IN'"));
        conditionOperation.getProducer().create(builder, property, expr);
    }


    private void processExpression(final OmSqlParser.MatchExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);

        final Operation conditionOperation = Operation.byOpCode("match")
                .getOrElseThrow(() -> new SqlParsingException("Unsupported operation 'IN SELECT'"));
        conditionOperation.getProducer().create(builder, property, expr);
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
        final Operation conditionOperation = Operation.byOpCode(operation)
                .getOrElseThrow(() -> new SqlParsingException("Unsupported operation '%s'", operation));
        conditionOperation.getProducer().create(builder, property, expr);
    }
}
