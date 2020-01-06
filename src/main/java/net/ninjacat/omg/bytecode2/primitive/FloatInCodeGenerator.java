/*
 * omg: IntInCodeGenerator.java
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

package net.ninjacat.omg.bytecode2.primitive;

import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.F2D;

public class FloatInCodeGenerator<T> extends FloatingPointInCodeGenerator<T, Float> {

    private static final String VALUE_OF_DESC = Type.getMethodDescriptor(Type.getType(Double.class), Type.getType(double.class));

    public FloatInCodeGenerator(final CodeGenerationContext context) {
        super(context);
    }

    @Override
    protected void boxIfNeeded(final Property<T, Float> property, final MethodVisitor method) {
        method.visitInsn(F2D);
        method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", VALUE_OF_DESC, false);
    }

}
