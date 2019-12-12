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
import jdk.nashorn.internal.codegen.types.Type;
import net.ninjacat.omg.bytecode2.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.Codes;
import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

public class IntInCodeGenerator<T> implements TypedCodeGenerator<T, Integer, Collection<Integer>> {

    private static final Random random = new Random();
    private static final String GENERATOR_DESCRIPTOR = Type.getMethodDescriptor(Collection.class);

    private final CodeGenerationContext<T> context;

    public IntInCodeGenerator(final CodeGenerationContext<T> context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If condition collection is empty no code will be generated. Otherwise static method to return Collection&lt;Integer&gt;
     * will be generated which returns collection in question
     * <p>
     * TODO: replace method call at comparision site with
     * a) static field initialized in static initializer
     * b) call to memoized supplier, so that collection is only initialized once.
     *
     * @param property  Property that needs to be matched
     * @param condition Condition describing how to match the property
     * @param method    {@link MethodVisitor} to generate the code in
     */
    @Override
    public void prepareStackForCompare(final Property<T, Integer> property, final PropertyCondition<Collection<Integer>> condition, final MethodVisitor method) {
        final Collection<Integer> value = condition.getValue();
        if (value.isEmpty()) {
            return; // no code needed if checking against empty collection
        }
        // create collection generation method and call it to put matching value on a stack
        final String generatorName = "getCollection" + "_" + property.getPropertyName() + "_" + Long.toHexString(random.nextLong());
        createGetCollectionMethod(generatorName, value);
        method.visitMethodInsn(Opcodes.INVOKESTATIC, context.matcherClassName(), generatorName, GENERATOR_DESCRIPTOR, false);

        getPropertyValue(property, method);
    }

    /**
     * @param values
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

    @Override
    public void getPropertyValue(final Property<T, Integer> property, final MethodVisitor method) {
        method.visitVarInsn(Opcodes.ALOAD, 0); // property is always local #0
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(property.getOwner()),
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                property.isInterface());
        // box value for collection.contains check
        method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", Type.getMethodDescriptor(Integer.class, int.class), false);
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<Collection<Integer>> condition, final MethodVisitor method) {
        // do nothing, all is done in .prepareStackForCompare()
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
                    Type.getMethodDescriptor(boolean.class, Object.class),
                    true);
        }
    }
}
