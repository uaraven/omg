/*
 * omg: CompilerProvider.java
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

package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.bytecode.primitive.*;
import net.ninjacat.omg.bytecode.reference.*;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

final class CompilerProvider {

    private CompilerProvider() {
    }

    static <T> PropertyPatternGenerator<T> getPatternGenerator(final Property<T> property, final PropertyCondition condition) {
        final PatternCompilerStrategy strategy = getStrategyFor(property.getType(), condition.getMethod());
        return new PropertyPatternGenerator<>(property, condition, strategy);
    }

    @SuppressWarnings("FeatureEnvy")
    private static PatternCompilerStrategy getStrategyFor(final Class cls, final ConditionMethod method) {
        return Match(cls).of(
                Case($(is(int.class)), intCls -> IntStrategy.forMethod(method, int.class)),
                Case($(is(short.class)), intCls -> IntStrategy.forMethod(method, short.class)),
                Case($(is(byte.class)), intCls -> IntStrategy.forMethod(method, byte.class)),
                Case($(is(char.class)), intCls -> IntStrategy.forMethod(method, char.class)),
                Case($(is(long.class)), l -> PrimitiveLongStrategy.forMethod(method)),
                Case($(is(float.class)), l -> PrimitiveFloatStrategy.forMethod(method)),
                Case($(is(double.class)), l -> PrimitiveDoubleStrategy.forMethod(method)),
                Case($(is(boolean.class)), l -> PrimitiveBooleanStrategy.forMethod(method)),
                Case($(is(Integer.class)), intCls -> IntegerStrategy.forMethod(method)),
                Case($(is(Long.class)), longCls -> LongStrategy.forMethod(method)),
                Case($(is(Short.class)), s -> ShortStrategy.forMethod(method)),
                Case($(is(Byte.class)), s -> ByteStrategy.forMethod(method)),
                Case($(is(Double.class)), s -> DoubleStrategy.forMethod(method)),
                Case($(is(Float.class)), s -> FloatStrategy.forMethod(method)),
                Case($(is(Character.class)), s -> CharacterStrategy.forMethod(method)),
                Case($(is(String.class)), s -> StringStrategyProvider.forMethod(method)),
                Case($(is(Boolean.class)), s -> BooleanStrategy.forMethod(method)),
                Case($((Predicate<Class>) Enum.class::isAssignableFrom), e -> EnumStrategy.forMethod(method)),
                Case($(), $_ -> ObjectStrategyProvider.forMethod(cls, method))
        );
    }


}
