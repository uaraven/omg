/*
 * omg: TypedCodeGenerator.java
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

package net.ninjacat.omg.bytecode2;

import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.conditions.PropertyCondition;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Interface for a matcher code generator.
 *
 * @param <T> Type of the target class
 * @param <P> Type of the property of the target class
 * @param <V> Type of the condition value (usually either P or Collection&lt;P&gt;)
 */
public interface TypedCodeGenerator<T, P, V> {

    /**
     * Generate synthetic helper methods. This is called before any other code is generated
     * and allows to create synthetic fields and methods needed
     */
    default void generateHelpers(final Property<T, P> property, final PropertyCondition<V> condition) {
        // Do nothing by default
    }

    /**
     * Retrieves value of the property and leaves it on stack
     */
    default void getPropertyValue(final Property<T, P> property, final MethodVisitor method) {
        method.visitVarInsn(Opcodes.ALOAD, Codes.MATCHED_LOCAL); // property is always local #2
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(property.getOwner()),
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                property.isInterface());
    }

    /**
     * Retrieves value of the matching constant and leaves it on stack
     * This can be any operation from simple CONST_XX to LDC or calling a synthetic method to create Pattern or Set
     */
    void getMatchingConstant(PropertyCondition<V> condition, MethodVisitor method);

    /**
     * Generates the code to compare two values on a stack using rules for the specific condition and
     * data type.
     * <p>
     * Result of this method must be a single int value on the stack, <strong>1</strong> if property value
     * has matched the condition or <strong>0</strong> if it has not.
     *
     * @param condition {@link PropertyCondition} to check
     * @param method    Method visitor to generate the code.
     */
    void compare(PropertyCondition<V> condition, MethodVisitor method);

    /**
     * Loads constant and property value onto stack for comparison
     * Default implementation loads the constant first followed by property value (using {@link #getMatchingConstant(PropertyCondition, MethodVisitor)}
     * and {@link #getPropertyValue(Property, MethodVisitor)} methods.
     *
     * @param property  Property that needs to be matched
     * @param condition Condition describing how to match the property
     * @param method    {@link MethodVisitor} to generate the code in
     */
    default void prepareStackForCompare(final Property<T, P> property, final PropertyCondition<V> condition, final MethodVisitor method) {
        generateHelpers(property, condition);
        getMatchingConstant(condition, method);
        getPropertyValue(property, method);
    }
}
