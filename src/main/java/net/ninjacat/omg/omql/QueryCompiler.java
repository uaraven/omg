package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.AlwaysTrueCondition;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.omql.parser.OmqlLexer;
import net.ninjacat.omg.omql.parser.OmqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.immutables.value.Value;

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

    public static QueryCompiler of(final String query, final Collection<Class<?>> registeredSources) {
        final OmqlLexer lexer = new OmqlLexer(CharStreams.fromString(query));
        final OmqlParser parser = new OmqlParser(new CommonTokenStream(lexer));
        final CompilerErrorListener errorListener = new CompilerErrorListener();
        parser.addErrorListener(errorListener);
        final OmqlParser.FilterContext tree = parser.filter();
        if (errorListener.hasErrors()) {
            throw new OmqlParsingException("Failed to parse query: \n%s", errorListener.toString());
        }

        return new QueryCompiler(tree.sql_stmt().select(), new RegisteredQuerySources(registeredSources));
    }

    static QueryCompiler ofParsed(final OmqlParser.SelectContext select, final RegisteredQuerySources registeredSources) {
        return new QueryCompiler(select, registeredSources);
    }

    private QueryCompiler(final OmqlParser.SelectContext selectContext,
                          final RegisteredQuerySources registeredSources) {
        this.where = selectContext.where();
        this.context = ImmutableQueryContext.builder()
                .registeredSources(registeredSources)
                .validator(new ClassValidator(registeredSources.getSource(selectContext.source_name().getText())))
                .build();
    }

    public Condition getCondition() {
        final Conditions.LogicalConditionBuilder builder = Conditions.matcher();

        if (where == null) {
            return AlwaysTrueCondition.INSTANCE;
        }
        final OmqlParser.ExprContext expr = where.expr();
        processExpression(expr, builder);

        return builder.build();
    }

    private void processExpression(final OmqlParser.ExprContext expr, final Conditions.LogicalConditionBuilder builder) {
        final Optional<PropertyContext> propertyContext = getPropertyContext(expr);
        if (propertyContext.isPresent() && propertyContext.get().isSubfield()) {
            processSubfield(propertyContext.get(), builder);
        } else {
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
        final String property = Optional.ofNullable(expr.field_name()).map(RuleContext::getText).get();
        final Operation conditionOperation = Operation.byOpCode(operation)
                .getOrElseThrow(() -> new OmqlParsingException("Unsupported operation '%s'", operation));
        conditionOperation.getProducer().create(builder, property, context, expr);
    }

    private static Optional<PropertyContext> getPropertyContext(final OmqlParser.ExprContext expr) {
        return Optional.ofNullable(Match(expr).of(
                Case($(instanceOf(OmqlParser.ConditionContext.class)), ctx -> ImmutablePropertyContext.of(
                        sourceText(ctx.field_name()),
                        sourceText(ctx.operator()),
                        sourceText(ctx.literal_value()))),
                Case($(instanceOf(OmqlParser.InExprContext.class)), ctx -> ImmutablePropertyContext.of(
                        sourceText(ctx.field_name()),
                        ctx.K_IN().getText(),
                        sourceText(ctx.list()))),
                Case($(instanceOf(OmqlParser.MatchExprContext.class)), ctx -> ImmutablePropertyContext.of(
                        sourceText(ctx.field_name()),
                        ctx.K_IN().getText(),
                        "(" + sourceText(ctx.select()) + ")")),
                Case($(), () -> null)
        ));
    }

    private static String sourceText(final ParserRuleContext context) {
        final int a = context.start.getStartIndex();
        final int b = context.stop.getStopIndex();
        final Interval interval = new Interval(a, b);
        return context.start.getInputStream().getText(interval);
    }

    private void processSubfield(final PropertyContext property, final Conditions.LogicalConditionBuilder builder) {
        if (!context.validator().isObjectProperty(property.getMasterField())) {
            throw new OmqlParsingException("Unsupported property type: '%s' in expression", property.getFullExpression());
        }

        final Class<?> returnType = context.validator().getReturnType(property.getMasterField());
        final String subExpression = property.getSubExpression(returnType);
        final QueryCompiler compiler = QueryCompiler.of(subExpression, returnType);
        builder.property(property.getMasterField()).match(compiler.getCondition());
    }

    @Value.Immutable
    interface PropertyContext {
        @Value.Parameter(order = 1)
        String propertyName();

        @Value.Parameter(order = 2)
        String operation();

        @Value.Parameter(order = 3)
        String value();

        default boolean isSubfield() {
            return propertyName().contains(".");
        }

        default String getMasterField() {
            final int dotPos = propertyName().indexOf('.');
            return propertyName().substring(0, dotPos);
        }

        default String getSubExpression(final Class<?> type) {
            final int dotPos = propertyName().indexOf('.');
            final String subField = propertyName().substring(dotPos + 1, propertyName().length());
            return String.format("SELECT * FROM %s WHERE %s %s %s", type.getSimpleName(), subField, operation(), value());
        }

        default String getFullExpression() {
            return String.format("%s %s %s", propertyName(), operation(), value());
        }
    }
}
