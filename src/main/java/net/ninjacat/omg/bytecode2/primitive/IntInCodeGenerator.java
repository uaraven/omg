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

public class IntInCodeGenerator<T> implements TypedCodeGenerator<T, Integer, Collection<Integer>> {

    private static final String GENERATOR_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Collection.class));
    private static final String FIELD_DESCRIPTOR = Type.getDescriptor(Collection.class);
    private static final String VALUE_OF_DESC = Type.getMethodDescriptor(Type.getType(Integer.class), Type.getType(int.class));
    private static final String FIELD_NAME = "fieldName";
    private static final String EMPTY = "empty";

    private final CodeGenerationContext context;

    public IntInCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void getPropertyValue(final Property<T, Integer> property, final MethodVisitor method) {
        if (!context.props().get(EMPTY, Boolean.class)) {
            method.visitVarInsn(Opcodes.ALOAD, Codes.MATCHED_LOCAL);
            method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getInternalName(property.getOwner()),
                    property.getMethod().getName(),
                    property.getMethod().getDescriptor(),
                    property.isInterface());
            // box value for collection.contains check
            method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", VALUE_OF_DESC, false);
        }
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<Collection<Integer>> condition, final MethodVisitor method) {
        if (!context.props().get(EMPTY, Boolean.class)) {
            final String fieldName = context.props().propAsString(FIELD_NAME);
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(GETFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
        }
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
    public void compare(final PropertyCondition<Collection<Integer>> condition, final MethodVisitor method) {
        if (condition.getMethod() != ConditionMethod.IN) {
            throw new CompilerException("Unsupported Condition for int type: %s", condition);
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
    public void generateHelpers(final Property<T, Integer> property, final PropertyCondition<Collection<Integer>> condition) {
        final Collection<Integer> value = condition.getValue();
        if (value.isEmpty()) {
            context.props().prop(EMPTY, true);
            return;
        }
        context.props().prop(EMPTY, false);
        // create collection generation method and call it to put matching value on a stack
        final String fieldName = "collection" + "_" + property.getPropertyName() + "_" + Long.toHexString(Codes.RNDG.nextLong());
        final String generatorName = "getC" + fieldName.substring(1);

        context.props().prop(FIELD_NAME, fieldName);

        Codes.createField(context.classVisitor(), fieldName, Collection.class);
        createGetCollectionMethod(generatorName, value);

        context.props().postConstructor((constructor, context) -> {
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(Opcodes.INVOKESTATIC, context.matcherClassName(), generatorName, GENERATOR_DESCRIPTOR, false);
            constructor.visitFieldInsn(PUTFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
        });

    }

    /**
     * @param values Collection of values
     */
    private void createGetCollectionMethod(final String methodName, final Collection<Integer> values) {
        final MethodVisitor generator = context.classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);

        generator.visitCode();
        // create array
        Codes.pushInt(generator, values.size());
        generator.visitIntInsn(NEWARRAY, T_INT);
        // push all values into the array
        Stream.ofAll(values).forEachWithIndex((val, idx) -> {
            generator.visitInsn(DUP);
            Codes.pushInt(generator, idx);
            Codes.pushInt(generator, val);
            generator.visitInsn(IASTORE);
        });
        // convert array into stream and collect into set
        generator.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "stream", "([I)Ljava/util/stream/IntStream;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/IntStream", "boxed", "()Ljava/util/stream/Stream;", true);
        generator.visitMethodInsn(INVOKESTATIC, "java/util/stream/Collectors", "toSet", "()Ljava/util/stream/Collector;", false);
        generator.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
        generator.visitTypeInsn(CHECKCAST, "java/util/Collection");
        generator.visitInsn(ARETURN);
        generator.visitMaxs(0, 0);
        generator.visitEnd();
    }
}
