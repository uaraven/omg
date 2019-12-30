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

import io.vavr.collection.Stream;
import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.bytecode2.generator.InCollectionCodeGenerator;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.function.ObjIntConsumer;

import static org.objectweb.asm.Opcodes.*;

public class DoubleInCodeGenerator<T> extends InCollectionCodeGenerator<T, Double> {

    private static final String VALUE_OF_DESC = Type.getMethodDescriptor(Type.getType(Double.class), Type.getType(double.class));

    public DoubleInCodeGenerator(final CodeGenerationContext context) {
        super(context);
    }

    @Override
    protected void boxIfNeeded(final Property<T, Double> property, final MethodVisitor method) {
        method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", VALUE_OF_DESC, false);
    }

    /**
     * @param values Collection of values
     */
    @Override
    protected void createGetCollectionMethod(final String methodName, final Collection<Double> values) {
        final MethodVisitor generator = getContext().classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);

        generator.visitCode();
        // create array
        Codes.pushInt(generator, values.size());
        generator.visitIntInsn(NEWARRAY, T_DOUBLE);
        // push all values into the array
        Stream.ofAll(values).forEachWithIndex(new ObjIntConsumer<Double>() {
            @Override
            public void accept(final Double val, final int idx) {
                generator.visitInsn(DUP);
                Codes.pushInt(generator, idx);
                Codes.pushDouble(generator, val);
                generator.visitInsn(DASTORE);
            }
        });

        // convert array into stream and collect into set
        generator.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "stream", "([D)Ljava/util/stream/DoubleStream;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/DoubleStream", "boxed", "()Ljava/util/stream/Stream;", true);
        generator.visitMethodInsn(INVOKESTATIC, "java/util/stream/Collectors", "toSet", "()Ljava/util/stream/Collector;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
        generator.visitTypeInsn(CHECKCAST, "java/util/Collection");
        generator.visitInsn(ARETURN);

        generator.visitMaxs(0, 0);
        generator.visitEnd();
    }
}
