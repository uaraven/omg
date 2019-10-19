/*
 * omg: PatternCompilerStrategy.java
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

package net.ninjacat.omg.bytecode;


import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface PatternCompilerStrategy {

    /**
     * Returns class to be used as parent for generated {@link PropertyPattern}.
     * <p>
     * Class must have the same constructor as {@link BasePropertyPattern}
     *
     * @return Class to be used as parent for generated property pattern.
     */
    default Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return BasePropertyPattern.class;
    }

    /**
     * Generates code to read property.
     * <p>
     * At the moment of call, instance reference will be on JVM stack. Generated code must leave property value on stack.
     *
     * @param mv       {@link MethodVisitor} to generate code
     * @param property Reference to a {@link Property}
     */
    @SuppressWarnings("FeatureEnvy")
    default void generatePropertyGet(final MethodVisitor mv, final Property property) {
        final String internalName = Type.getInternalName(property.getOwner());
        mv.visitTypeInsn(Opcodes.CHECKCAST, internalName);
        mv.visitMethodInsn(
                property.isInterface() ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                internalName,
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                property.isInterface());
    }

    /**
     * Should generate code to compare two objects
     * <p>
     * At the moment of call, JVM stack contains:
     * <pre>
     *     - matching value from pattern (unboxed in case of primitive)
     *     - actual property value from instance being matched
     * </pre>
     * After the call stack must contain either ICONST_0 or ICONST_1, i.e. {@code false} or {@code true}
     *
     * @param mv {@link MethodVisitor} to generate code
     */
    void generateCompareCode(MethodVisitor mv);

    /**
     * Opcode for storing property value from stack to local variable
     *
     * @return Opcode for storing property value from stack to local variable
     */
    int store();

    /**
     * Opcode for loading property value from local variable to stack
     *
     * @return Opcode for loading property value from local variable to stack
     */
    int load();

    /**
     * returns true if property type is reference
     *
     * @return true if property type is reference
     */
    boolean isReference();

    /**
     * Returns method descriptor for getMatchingValue() method
     * <p>
     * Default value is ""()Ljava/lang/Object;" and rarely has to be changed
     *
     * @return method descriptor for getMatchingValue() method
     */
    default String getMatchingValueDescriptor() {
        return "()Ljava/lang/Object;";
    }

    /**
     * Returns local variable index for property value. Should not be less than 2. It is not a good idea to
     * override default value.
     *
     * @return local variable index for property value
     */
    default int getPropertyLocalIndex() {
        return 2;
    }

    /**
     * Returns local variable index for matching value reference. Should not be less than 3.
     * Default value is fine for almost every implementation except when property type takes two slots in
     * local variables (i.e. long or double)
     *
     * @return local variable index for matching value reference.
     */
    default int getMatchingLocalIndex() {
        return 3;
    }

    /**
     * Called before comparision code is generated, specifically before
     * <b>*load</b>s for matching value and property value are executed
     *
     * @param match Method visitor
     */
    default void beforeCompare(final MethodVisitor match) {
    }

    /**
     * Opcode for storing matching value to local variable. For most cases default implementation, which matches
     * {@link #store()} is fine. The only exception currently is IN condition for primitive types, where matching value
     * is list and property value is primitive type.
     *
     * @return Opcode for storing matching value to local variable
     */
    default int matchingStore() {
        return store();
    }

    /**
     * Opcode for loading matching value from local variable. For most cases default implementation, which matches
     * {@link #load()} is fine. The only exception currently is IN condition for primitive types, where matching value
     * is list and property value is primitive type.
     *
     * @return Opcode for loading matching value from local variable
     */
    default int matchingLoad() {
        return load();
    }

    /**
     * Returns order in which operands will be pushed to stack for compare operation.
     * <p>
     * Default ordering is {@link CompareOrdering#PROPERTY_THEN_MATCHING}
     *
     * @return {@link CompareOrdering}
     */
    default CompareOrdering compareOrdering() {
        return CompareOrdering.PROPERTY_THEN_MATCHING;
    }
}
