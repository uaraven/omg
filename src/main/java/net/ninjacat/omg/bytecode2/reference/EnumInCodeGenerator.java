/*
 * omg: StringInCodeGenerator.java
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

import io.vavr.collection.Stream;
import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.bytecode2.generator.InCollectionCodeGenerator;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;

public class EnumInCodeGenerator<T> extends InCollectionCodeGenerator<T, Enum<?>> {

    public EnumInCodeGenerator(final CodeGenerationContext context) {
        super(context);
    }

    @Override
    protected void boxIfNeeded(final Property<T, Enum<?>> property, final MethodVisitor method) {
        // boxing is not needed
    }

    /**
     * @param values Collection of values
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void createGetCollectionMethod(final String methodName, final Collection<Enum<?>> values) {
        final MethodVisitor generator = getContext().classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);

        generator.visitCode();
        // create array
        Codes.pushInt(generator, values.size());
        generator.visitTypeInsn(ANEWARRAY, Type.getInternalName(Object.class));
        // push all values into the array
        Stream.ofAll(values).forEachWithIndex((val, idx) -> {
            generator.visitInsn(DUP);
            Codes.pushInt(generator, idx);
            Codes.pushEnum(generator, (Class<Enum>) val.getClass(), val.name());
            generator.visitInsn(AASTORE);
        });
        // convert array into stream and collect into set
        generator.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "stream", "()Ljava/util/stream/Stream;", true);
        generator.visitMethodInsn(INVOKESTATIC, "java/util/stream/Collectors", "toSet", "()Ljava/util/stream/Collector;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
        generator.visitTypeInsn(CHECKCAST, "java/util/Collection");

        generator.visitInsn(ARETURN);
        generator.visitMaxs(0, 0);
        generator.visitEnd();
    }
}
