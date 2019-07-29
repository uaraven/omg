package net.ninjacat.objmatcher.matcher.patterns;

import net.ninjacat.objmatcher.matcher.conditions.AndCondition;
import net.ninjacat.objmatcher.matcher.conditions.Condition;
import net.ninjacat.objmatcher.matcher.conditions.OrCondition;
import net.ninjacat.objmatcher.matcher.conditions.PropertyCondition;

import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public final class Patterns {

    private Patterns() {
    }

    public static <T> Pattern<T> build(final Condition condition, final PropertyPatternBuilder<T> propBuilder) {
        return processCondition(condition, propBuilder);
    }

    @SuppressWarnings("unchecked")
    private static <T> Pattern<T> processCondition(final Condition condition, final PropertyPatternBuilder<T> propBuilder) {
        return Match(condition).of(
                Case($(instanceOf(AndCondition.class)), andCondition -> processAndCondition(andCondition, propBuilder)),
                Case($(instanceOf(OrCondition.class)), orCondition -> processOrCondition(orCondition, propBuilder)),
                Case($(instanceOf(PropertyCondition.class)), propCondition -> processPropertyCondition(propCondition, propBuilder)),
                Case($(), o -> {
                    throw new IllegalStateException("Unexpected condition: " + o.toString());
                })
        );
    }

    private static <T, P> PropertyPattern<T> processPropertyCondition(final PropertyCondition<P> condition, final PropertyPatternBuilder<T> propBuilder) {
        return propBuilder.build(condition);
    }

    private static <T> Pattern<T> processAndCondition(final AndCondition condition, final PropertyPatternBuilder<T> propBuilder) {
        final List<Pattern<T>> patterns = condition.getChildren().stream().map(cond -> processCondition(cond, propBuilder)).collect(Collectors.toList());
        return new AndPattern<>(patterns);
    }

    private static <T> Pattern<T> processOrCondition(final OrCondition condition, final PropertyPatternBuilder<T> propBuilder) {
        final List<Pattern<T>> patterns = condition.getChildren().stream().map(cond -> processCondition(cond, propBuilder)).collect(Collectors.toList());
        return new OrPattern<>(patterns);
    }
}
