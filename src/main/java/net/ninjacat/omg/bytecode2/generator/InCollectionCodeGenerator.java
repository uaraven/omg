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

package net.ninjacat.omg.bytecode2.generator;

import net.ninjacat.omg.bytecode2.Property;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;

/**
 * Basic implementation of code generator for IN condition.
 * <p>
 * IN checks for all types follow the same pattern: create static collection field with literal values, call
 * {@link Collection#contains(Object)} to check if property value is in the collection.
 * <p>
 * Customizations needed for different types:
 * <ul>
 *     <li>creation of actual collection</li>
 *     <li>potentially boxing primitive values before calling "contains"</li>
 * </ul>
 *
 * @param <T> Type of object that the matcher is generated for
 * @param <P> Type of the property being checked
 */
public abstract class InCollectionCodeGenerator<T, P> implements TypedCodeGenerator<T, P, Collection<P>> {

    protected static final String GENERATOR_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Collection.class));
    protected static final String FIELD_DESCRIPTOR = Type.getDescriptor(Collection.class);
    protected static final String FIELD_NAME = "fieldName";
    protected static final String EMPTY = "empty";

    private final CodeGenerationContext context;

    public InCollectionCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    public CodeGenerationContext getContext() {
        return context;
    }

    @Override
    public void getPropertyValue(final Property<T, P> property, final MethodVisitor method) {
        if (!context.props().get(EMPTY, Boolean.class)) {
            method.visitVarInsn(Opcodes.ALOAD, Codes.MATCHED_LOCAL);
            method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    Type.getInternalName(property.getOwner()),
                    property.getMethod().getName(),
                    property.getMethod().getDescriptor(),
                    property.isInterface());
            boxIfNeeded(property, method);
        }
    }

    /**
     * Implementations need to override this method if property value needs to be boxed before the call to
     * {@link Collection#contains(Object)}.
     * <p>
     * Implementation can be as simple as call to Integer.valueOf() or similar.
     * <p>
     * Value that needs to be boxed will be on the stack when this method is called.
     *
     * @param property {@link Property}
     * @param method   Method visitor to generate code.
     */
    protected abstract void boxIfNeeded(Property<T, P> property, MethodVisitor method);

    @Override
    public void getMatchingConstant(final PropertyCondition<Collection<P>> condition, final MethodVisitor method) {
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
    public void compare(final PropertyCondition<Collection<P>> condition, final MethodVisitor method) {
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
    public void generateHelpers(final Property<T, P> property, final PropertyCondition<Collection<P>> condition) {
        final Collection<P> value = condition.getValue();
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
     * Implementations need to override this method to generate method that will create a collection of values.
     * <p>
     * The generated method must be static (and it is recommended for it to be synthetic) and it must return
     * {@link Collection}. Recommended implementation of collection is a {@code Set} as it allows fastest "contains"
     * checks.
     * <p>
     * Generated method must be free of any side-effects and must not make any assumptions of object state. It will
     * be called inside constructor.
     *
     * @param methodName Name of the method to be generated
     * @param values     Collection of values
     */
    protected abstract void createGetCollectionMethod(final String methodName, final Collection<P> values);
}
