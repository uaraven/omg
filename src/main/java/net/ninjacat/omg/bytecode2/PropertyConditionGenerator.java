/*
 * omg: PropertyConditionGenerator.java
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

import net.ninjacat.omg.bytecode2.primitive.IntGeneratorProvider;
import net.ninjacat.omg.conditions.PropertyCondition;
import org.objectweb.asm.MethodVisitor;

import static io.vavr.API.*;

public class PropertyConditionGenerator<T, P, V> implements ConditionCodeGenerator<T, PropertyCondition<V>> {

    @Override
    public void generateCode(final CodeGenerationContext<T> context,
                             final MethodVisitor method,
                             final PropertyCondition<V> condition) {
        final Property<T, P> property = createProperty(condition.getProperty(), context.targetClass());

        final TypedCodeGenerator<T, P, V> codeGen = getGeneratorFor(property.getType(), condition, context);

        codeGen.prepareStackForCompare(property, condition, method);
        codeGen.compare(condition, method);
    }

    private TypedCodeGenerator<T, P, V> getGeneratorFor(final Class<?> type, final PropertyCondition<V> condition, final CodeGenerationContext<T> context) {
        return Match(type).of(
                Case($(), x -> (TypedCodeGenerator<T, P, V>) IntGeneratorProvider.getGenerator(condition, context))
        );
    }

    private Property<T, P> createProperty(final String field, final Class<T> targetClass) {
        return Property.fromPropertyName(field, targetClass);
    }
}
