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
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;

public class StringInCodeGenerator<T> implements TypedCodeGenerator<T, String, Collection<String>> {

    private static final String GENERATOR_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Collection.class));
    private static final String FIELD_DESCRIPTOR = Type.getDescriptor(Collection.class);
    private static final String FIELD_NAME = "fieldName";

    private final CodeGenerationContext context;

    public StringInCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }


    @Override
    public void getPropertyValue(final Property<T, String> property, final MethodVisitor method) {
        method.visitVarInsn(Opcodes.ALOAD, Codes.MATCHED_LOCAL);
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(property.getOwner()),
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                property.isInterface());
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<Collection<String>> condition, final MethodVisitor method) {
        final String fieldName = context.props().propAsString(FIELD_NAME);
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If matching collection is empty, simple {@code ICONST_0} will be generated,
     * otherwise call to {@link Collection#contains(Object)} will be used.
     *
     * @param condition {@link PropertyCondition} to check
     * @param method    Method visitor to generate the code.
     */
    @Override
    public void compare(final PropertyCondition<Collection<String>> condition, final MethodVisitor method) {
        if (condition.getMethod() != ConditionMethod.IN) {
            throw new CompilerException("Unsupported Condition for String type: %s", condition);
        }
        if (condition.getValue().isEmpty()) {
            method.visitInsn(Opcodes.ICONST_0); // if target collection is empty, matching always fails
        } else {
            method.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                    Type.getInternalName(Collection.class),
                    "contains",
                    Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class)),
                    true);
        }
    }

    @Override
    public void generateHelpers(final Property<T, String> property, final PropertyCondition<Collection<String>> condition) {
        if (condition.getValue().isEmpty()) {
            context.props().prop("empty", true); // do not generate any code if collection is empty
            return;
        }
        context.props().prop("empty", false);
        final String fieldName = "collection" + "_" + property.getPropertyName() + "_" + Long.toHexString(Codes.RNDG.nextLong());
        context.props().prop(FIELD_NAME, fieldName);
        final String generatorName = "getC" + fieldName.substring(1);

        Codes.createField(context.classVisitor(), fieldName, Collection.class);
        createGetCollectionMethod(generatorName, condition.getValue());

        context.props().postConstructor((constructor, context) -> {
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(Opcodes.INVOKESTATIC, context.matcherClassName(), generatorName, GENERATOR_DESCRIPTOR, false);
            constructor.visitFieldInsn(PUTFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
        });
    }

    /**
     * @param values Collection of values
     */
    private void createGetCollectionMethod(final String methodName, final Collection<String> values) {
        final MethodVisitor generator = context.classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);

        generator.visitCode();
        // create array
        Codes.pushInt(generator, values.size());
        generator.visitTypeInsn(ANEWARRAY, Type.getInternalName(String.class));
        // push all values into the array
        Stream.ofAll(values).forEachWithIndex((val, idx) -> {
            generator.visitInsn(DUP);
            Codes.pushInt(generator, idx);
            generator.visitLdcInsn(val);
            generator.visitInsn(AASTORE);
        });
        // convert array into stream and collect into set
        generator.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "stream", "([Ljava/lang/Object;)Ljava/util/stream/Stream;", false);
        generator.visitMethodInsn(INVOKESTATIC, "java/util/stream/Collectors", "toSet", "()Ljava/util/stream/Collector;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
        generator.visitTypeInsn(CHECKCAST, "java/util/Collection");
        generator.visitInsn(ARETURN);
        generator.visitMaxs(0, 0);
        generator.visitEnd();
    }
}
