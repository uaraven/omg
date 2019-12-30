/*
 * omg: ObjectEqCodeGenerator.java
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

import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Generates code to perform equalTo/notEqualTo comparision on objects. Relies on properly implemented
 * {@link Object#equals(Object)} method
 *
 * @param <T>
 * @param <P>
 */
public class ObjectEqCodeGenerator<T, P> implements TypedCodeGenerator<T, P, P> {
    private final CodeGenerationContext context;

    public ObjectEqCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<P> condition, final MethodVisitor method) {
        method.visitLdcInsn(condition.getValue());
    }

    @Override
    public void compare(final PropertyCondition<P> condition, final MethodVisitor method) {
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Codes.OBJECT_NAME,
                "equals",
                Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class)),
                false);
        if (condition.getMethod() == ConditionMethod.NEQ) {
            Codes.logicalNot(method);
        }

    }
}
