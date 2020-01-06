/*
 * omg: StringGeneratorProvider.java
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

package net.ninjacat.omg.bytecode2.reference;

import io.vavr.API;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

import java.util.EnumSet;
import java.util.Set;

import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

public final class EnumGeneratorProvider {
    private static final Set<ConditionMethod> SUPPORTED_METHODS = EnumSet.of(
            ConditionMethod.EQ,
            ConditionMethod.NEQ,
            ConditionMethod.IN,
            ConditionMethod.REGEX);

    private EnumGeneratorProvider() {
    }

    public static <T> TypedCodeGenerator<T, Enum<?>, ?> getGenerator(final Condition condition, final CodeGenerationContext context) {
        if (!SUPPORTED_METHODS.contains(condition.getMethod())) {
            throw new CompilerException("Condition {} is not supported for type 'enum'", condition);
        }
        return Match(condition.getMethod()).of(
                Case(API.$(is(ConditionMethod.REGEX)), x -> new ObjectRegexCodeGenerator<>(context)),
                Case(API.$(is(ConditionMethod.IN)), x -> new EnumInCodeGenerator<>(context)),
                Case(API.$(), $_ -> new EnumEqCodeGenerator<>(context))

        );
    }
}
