package net.ninjacat.omg.omql;

import io.vavr.control.Try;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.omql.parser.OmqlLexer;
import net.ninjacat.omg.omql.parser.OmqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;

import java.util.Collection;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static net.ninjacat.omg.omql.parser.OmqlParser.WhereContext;

@SuppressWarnings("OverlyCoupledClass")

public final class QueryCompiler {
    private final WhereContext where;
    private final QueryContext context;

    public static QueryCompiler of(final String query, final Class<?> allowedSource) {
        return of(query, io.vavr.collection.List.<Class<?>>of(allowedSource).asJava());
    }

    public static QueryCompiler of(final String query, final Class<?> sourceA, final Class<?> sourceB) {
        return of(query, io.vavr.collection.List.of(sourceA, sourceB).asJava());
    }

    public static QueryCompiler of(final String query, final Class<?> sourceA, final Class<?> sourceB, final Class<?> sourceC) {
        return of(query, io.vavr.collection.List.of(sourceA, sourceB, sourceC).asJava());
    }

    public static QueryCompiler of(final String query, final Class<?> sourceA, final Class<?> sourceB, final Class<?> sourceC, final Class<?> sourceD) {
        return of(query, io.vavr.collection.List.of(sourceA, sourceB, sourceC, sourceD).asJava());
    }

    public static QueryCompiler of(final String query, final Collection<Class<?>> allowedSources) {
        final OmqlLexer lexer = new OmqlLexer(CharStreams.fromString(query));
        final OmqlParser parser = new OmqlParser(new CommonTokenStream(lexer));
        final CompilerErrorListener errorListener = new CompilerErrorListener();
        parser.addErrorListener(errorListener);
        final OmqlParser.FilterContext tree = parser.filter();
        if (errorListener.hasErrors()) {
            throw new OmqlParsingException("Failed to parse query: \n%s", errorListener.toString());
        }

        return new QueryCompiler(tree.sql_stmt().select(), allowedSources);
    }

    private static Class getSource(final OmqlParser.SelectContext select, final Collection<Class<?>> allowedSources) {
        final String className = select.source_name().getText();
        return allowedSources.stream().filter(cl -> cl.getSimpleName().equals(className)).findFirst().orElseGet(
                () -> Try.of(() -> Class.forName(className))
                        .getOrElseThrow(thr -> new OmqlParsingException("Cannot find class '%s'", className))
        );
    }

    static QueryCompiler ofParsed(final OmqlParser.SelectContext select, final Collection<Class<?>> allowedSources) {
        return new QueryCompiler(select, allowedSources);
    }

    private QueryCompiler(final OmqlParser.SelectContext selectContext,
                          final Collection<Class<?>> allowedSources) {
        this.where = selectContext.where();
        this.context = ImmutableQueryContext.builder()
                .allowedSources(allowedSources)
                .validator(new ClassValidator(getSource(selectContext, allowedSources)))
                .build();
    }

    public Condition getCondition() {
        final Conditions.LogicalConditionBuilder builder = Conditions.matcher();

        final OmqlParser.ExprContext expr = where.expr();
        processExpression(expr, builder);

        return builder.build();
    }

    private void processExpression(final OmqlParser.ExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        Match(expr).of(
                Case($(instanceOf(OmqlParser.ConditionContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmqlParser.AndExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmqlParser.OrExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmqlParser.NotExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmqlParser.InExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(instanceOf(OmqlParser.MatchExprContext.class)), ctx -> run(() -> processExpression(ctx, builder))),
                Case($(), () -> {
                    throw new OmqlParsingException("Unsupported expression: %s", expr.getText());
                })
        );
    }

    private void processExpression(final OmqlParser.InExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);

        final Operation conditionOperation = Operation.byOpCode("in")
                .getOrElseThrow(() -> new OmqlParsingException("Unsupported operation 'IN'"));
        conditionOperation.getProducer().create(builder, property, context, expr);
    }


    private void processExpression(final OmqlParser.MatchExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);

        final Operation conditionOperation = Operation.byOpCode("match")
                .getOrElseThrow(() -> new OmqlParsingException("Unsupported operation 'IN SELECT'"));
        conditionOperation.getProducer().create(builder, property, context, expr);
    }

    private void processExpression(final OmqlParser.AndExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.and(cond -> {
                    processExpression(expr.expr(0), cond);
                    processExpression(expr.expr(1), cond);
                }
        );
    }

    private void processExpression(final OmqlParser.OrExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.or(cond -> {
                    processExpression(expr.expr(0), cond);
                    processExpression(expr.expr(1), cond);
                }
        );
    }

    private void processExpression(final OmqlParser.NotExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        builder.not(cond -> processExpression(expr.expr(), cond)
        );
    }

    private void processExpression(final OmqlParser.ConditionContext expr, final Conditions.LogicalConditionBuilder builder) {
        final String operation = Optional.ofNullable(expr.operator()).map(RuleContext::getText).orElse(null);
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).orElse(null);
        final Operation conditionOperation = Operation.byOpCode(operation)
                .getOrElseThrow(() -> new OmqlParsingException("Unsupported operation '%s'", operation));
        conditionOperation.getProducer().create(builder, property, context, expr);
    }
}
