/*
 * omg: ObjectMatchGenerator.java
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

import net.ninjacat.omg.bytecode2.BytecodeConditionCompiler;
import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

public class ObjectMatchGenerator<T, P, V> implements TypedCodeGenerator<T, P, V> {

    private final CodeGenerationContext context;

    public ObjectMatchGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getMatchingConstant(final PropertyCondition<V> condition, final MethodVisitor method) {
        final Property<T, P> property = Property.fromPropertyName(condition.getProperty(), context.targetClass());
        final Class<PropertyPattern<T>> patternClass = BytecodeConditionCompiler.forClass(property.getType()).getClass((Condition) condition.getValue());
        final String internalName = Type.getInternalName(patternClass);
        method.visitTypeInsn(Opcodes.NEW, internalName);
        method.visitInsn(Opcodes.DUP);
        method.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false);
    }

    @Override
    public void compare(final PropertyCondition<V> condition, final MethodVisitor method) {
        method.visitMethodInsn(INVOKEINTERFACE,
                Type.getInternalName(Pattern.class),
                "matches",
                Codes.getMethodDescriptor(boolean.class, Object.class),
                true);

    }
}
