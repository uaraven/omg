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

import jdk.nashorn.internal.codegen.types.Type;
import net.ninjacat.omg.bytecode2.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.Random;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

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
        final String name = createGetCollectionMethod(property, value);
        method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(context.targetClass()), name, GENERATOR_DESCRIPTOR, false);

        getPropertyValue(property, method);

    }

    private String createGetCollectionMethod(final Property<T, Integer> property, final Collection<Integer> value) {
        final String generatorName = "getCollection" + "_" + property.getPropertyName() + "_" + Long.toHexString(random.nextLong());
        // TODO: Generate collection

        final MethodVisitor generator = context.classVisitor().visitMethod(ACC_PRIVATE + ACC_STATIC, generatorName,
                GENERATOR_DESCRIPTOR, null, null);

        return generatorName;
    }

    @Override
    public void getPropertyValue(final Property<T, Integer> property, final MethodVisitor method) {
        method.visitVarInsn(Opcodes.ALOAD, 1); // property is always local #1
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
        // do nothing, all
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
