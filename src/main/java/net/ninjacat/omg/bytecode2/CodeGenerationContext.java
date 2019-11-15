/*
 * omg: CodeGenerationContext.java
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

import net.ninjacat.omg.conditions.LogicalCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import org.immutables.value.Value;
import org.objectweb.asm.ClassVisitor;

/**
 * Context for generating code for a matcher
 *
 * @param <T> Type of target class
 */
@Value.Immutable
public interface CodeGenerationContext<T> {
    ClassVisitor classVisitor();

    Class<T> targetClass();

    ConditionCodeGenerator<T, LogicalCondition> logicalConditionGenerator();

    ConditionCodeGenerator<T, PropertyCondition<T>> propertyConditionGenerator();
}

