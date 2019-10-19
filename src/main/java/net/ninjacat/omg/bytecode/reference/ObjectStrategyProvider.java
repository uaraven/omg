/*
 * omg: ObjectStrategyProvider.java
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

package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Provider for choosing strategy for objects matching based on ConditionMethod
 * <p>
 * Object support only EQ, NEQ and MATCH methods
 */
public final class ObjectStrategyProvider {
    private ObjectStrategyProvider() {
    }


    public static PatternCompilerStrategy forMethod(final Class<?> cls, final ConditionMethod method) {
        switch (method) {
            case EQ:
            case NEQ:
                return new ObjectStrategy(method);
            case MATCH:
                return new ObjectMatchStrategy();
            case IN:
                return new ReferenceInStrategy();
            case REGEX:
                return new ObjectRegexStrategy();
            default:
                throw new CompilerException("Unsupported condition '%s' for Object type", method);
        }
    }
}
