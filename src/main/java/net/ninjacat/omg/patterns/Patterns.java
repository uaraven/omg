/*
 * omg: Patterns.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.patterns;

import net.ninjacat.omg.conditions.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public final class Patterns {

    private Patterns() {
    }

    public static <T> Pattern<T> compile(final Condition condition, final PropertyPatternCompiler<T> propBuilder) {
        return processCondition(condition, propBuilder);
    }

    @SuppressWarnings("unchecked")
    private static <T> Pattern<T> processCondition(final Condition condition, final PropertyPatternCompiler<T> propBuilder) {
        return Match(condition).of(
                Case($(instanceOf(AlwaysTrueCondition.class)), trueCondition -> processAlwaysTrueCondition()),
                Case($(instanceOf(AndCondition.class)), andCondition -> processAndCondition(andCondition, propBuilder)),
                Case($(instanceOf(OrCondition.class)), orCondition -> processOrCondition(orCondition, propBuilder)),
                Case($(instanceOf(NotCondition.class)), notCondition -> processNotCondition(notCondition, propBuilder)),
                Case($(instanceOf(PropertyCondition.class)), propCondition -> processPropertyCondition(propCondition, propBuilder)),
                Case($(), o -> {
                    throw new IllegalStateException("Unexpected condition: " + o.toString());
                })
        );
    }

    private static <T, P> PropertyPattern<T> processPropertyCondition(final PropertyCondition<P> condition, final PropertyPatternCompiler<T> propBuilder) {
        return propBuilder.build(condition);
    }

    private static <T> Pattern<T> processAlwaysTrueCondition() {
        return AlwaysMatchingPattern.INSTANCE;
    }

    private static <T> Pattern<T> processAndCondition(final AndCondition condition, final PropertyPatternCompiler<T> propBuilder) {
        final List<Pattern<T>> patterns = condition.getChildren().stream().map(cond -> processCondition(cond, propBuilder)).collect(Collectors.toList());
        return new AndPattern<>(patterns);
    }

    private static <T> Pattern<T> processOrCondition(final OrCondition condition, final PropertyPatternCompiler<T> propBuilder) {
        final List<Pattern<T>> patterns = condition.getChildren().stream().map(cond -> processCondition(cond, propBuilder)).collect(Collectors.toList());
        return new OrPattern<>(patterns);
    }

    private static <T> Pattern<T> processNotCondition(final NotCondition condition, final PropertyPatternCompiler<T> propBuilder) {
        return new NotPattern<>(processCondition(condition.getChild(), propBuilder));
    }
}
