/*
 * omg: FloatingPointInCodeGenerator.java
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

package net.ninjacat.omg.bytecode2.primitive;

import io.vavr.collection.Stream;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.bytecode2.generator.InCollectionCodeGenerator;
import org.objectweb.asm.MethodVisitor;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;

/**
 * Base implementation if IN comparator code generator for floating point types.
 * <p>
 * Internally list of floating-point values is always represented as collection of {@link Double} and creation of
 * such collection is implemented in this class.
 * <p>
 * It is responsibility of child classes to perform correct boxing from {@code float} or {@code double} to {@code Double}
 *
 * @param <T>  Type of matched object
 * @param <FP> Type of property, should be either {@link Float} or {@link Double}
 */
public abstract class FloatingPointInCodeGenerator<T, FP extends Number> extends InCollectionCodeGenerator<T, FP> {

    public FloatingPointInCodeGenerator(final CodeGenerationContext context) {
        super(context);
    }

    /**
     * @param values Collection of values
     */
    @Override
    protected void createGetCollectionMethod(final String methodName, final Collection<FP> values) {
        final MethodVisitor generator = getContext().classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);

        generator.visitCode();
        // create array
        Codes.pushInt(generator, values.size());
        generator.visitIntInsn(NEWARRAY, T_DOUBLE);
        // push all values into the array
        Stream.ofAll(values).forEachWithIndex((val, idx) -> {
            generator.visitInsn(DUP);
            Codes.pushInt(generator, idx);
            Codes.pushDouble(generator, val.doubleValue());
            generator.visitInsn(DASTORE);
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
