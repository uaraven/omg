/*
 * omg: ComparableGeneratorProvider.java
 *
 * Copyright 2020 Oleksiy Voronin <me@ovoronin.info>
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

import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

import java.util.EnumSet;
import java.util.Set;

public final class ObjectGeneratorProvider {
    private static final Set<ConditionMethod> SUPPORTED_METHODS = EnumSet.of(
            ConditionMethod.REGEX,
            ConditionMethod.MATCH);

    private ObjectGeneratorProvider() {
    }

    public static <T> TypedCodeGenerator<T, Object, ?> getGenerator(final Condition condition, final CodeGenerationContext context) {
        if (!SUPPORTED_METHODS.contains(condition.getMethod())) {
            return fail(condition);
        }
        if (condition.getMethod() == ConditionMethod.REGEX) {
            return new ObjectRegexCodeGenerator<>(context);
        } else {
            return fail(condition);
        }
    }

    private static <T> TypedCodeGenerator<T, Object, ?> fail(final Condition condition) {
        throw new CompilerException("Condition '%s' is not supported for generic objects", condition.getMethod());
    }
}
