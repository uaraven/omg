/*
 * omg: AsmPatternCompiler.java
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

package net.ninjacat.omg.bytecode2;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.patterns.PropertyPatternCompiler;

/**
 * Compiles {@link Condition} into an instance of {@link Pattern}
 *
 * @param <T> Class for which condition is compiled
 */
public final class BytecodeConditionCompiler<T> implements PropertyPatternCompiler<T> {
    private final Class<T> cls;

    public static <T> BytecodeConditionCompiler<T> forClass(final Class<T> cls) {
        return new BytecodeConditionCompiler<>(cls);
    }

    private BytecodeConditionCompiler(final Class<T> cls) {
        this.cls = cls;
    }

    public PropertyPattern<T> build(final Condition condition) {
        return build(condition, CompilationOptions.getDefaults());
    }

    public PropertyPattern<T> build(final Condition condition, final CompilationOptions options) {
        final MatcherGenerator<T> compiler = new MatcherGenerator<>(cls, condition);
        return compiler.compilePattern(options);
    }

    public Class<PropertyPattern<T>> getClass(final Condition condition) {
        return getClass(condition, CompilationOptions.getDefaults());
    }

    public Class<PropertyPattern<T>> getClass(final Condition condition, final CompilationOptions options) {
        final MatcherGenerator<T> compiler = new MatcherGenerator<>(cls, condition);
        return compiler.compilePatternClass(options);
    }

    @Override
    public <P> PropertyPattern<T> build(final PropertyCondition<P> condition) {
        return build((Condition) condition);
    }
}
