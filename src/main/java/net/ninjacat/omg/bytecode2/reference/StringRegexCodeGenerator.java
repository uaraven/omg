/*
 * omg: StringRegexCodeGenerator.java
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

public class StringRegexCodeGenerator<T> implements TypedCodeGenerator<T, String, String> {
    private static final String GENERATOR_DESCRIPTOR = getMethodDescriptor(getType(Pattern.class));
    private static final String FIELD_DESCRIPTOR = getDescriptor(Pattern.class);
    private static final String FIELD_NAME = "fieldName";

    private final CodeGenerationContext context;

    public StringRegexCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void getPropertyValue(final Property<T, String> property, final MethodVisitor method) {
        method.visitVarInsn(Opcodes.ALOAD, Codes.MATCHED_LOCAL); // property is always local #2
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                getInternalName(property.getOwner()),
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                property.isInterface());
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<String> condition, final MethodVisitor method) {
        final String fieldName = context.props().propAsString(FIELD_NAME);
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
    }

    @Override
    public void compare(final PropertyCondition<String> condition, final MethodVisitor method) {
        if (condition.getMethod() != ConditionMethod.REGEX) {
            throw new CompilerException("Unsupported Condition for String type: %s", condition);
        }
        // call Pattern.matcher(<value>)
        method.visitMethodInsn(INVOKEVIRTUAL,
                getInternalName(Pattern.class),
                "matcher",
                Type.getMethodDescriptor(Type.getType(Matcher.class), Type.getType(CharSequence.class)),
                false);
        // call Matcher.matches()
        method.visitMethodInsn(INVOKEVIRTUAL,
                Type.getInternalName(Matcher.class),
                "matches",
                Type.getMethodDescriptor(Type.getType(boolean.class)),
                false);
    }

    /**
     * @param value String containing regex pattern
     */
    private void createGetRegexMethod(final String methodName, final String value) {
        final MethodVisitor generator = context.classVisitor()
                .visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, methodName,
                        GENERATOR_DESCRIPTOR, null, null);
        generator.visitCode();

        generator.visitLdcInsn(value);
        generator.visitMethodInsn(INVOKESTATIC,
                getInternalName(Pattern.class),
                "compile",
                getMethodDescriptor(getType(Pattern.class), getType(String.class)),
                false);
        generator.visitInsn(ARETURN);

        generator.visitMaxs(0, 0);
        generator.visitEnd();
    }

    @Override
    public void generateHelpers(final Property<T, String> property, final PropertyCondition<String> condition) {
        final String fieldName = "pattern" + "_" + property.getPropertyName() + "_" + Long.toHexString(Codes.RNDG.nextLong());
        context.props().prop(FIELD_NAME, fieldName);
        final String generatorName = "getP" + fieldName.substring(1);

        createGetRegexMethod(generatorName, condition.getValue());
        Codes.createField(context.classVisitor(), fieldName, Pattern.class);

        context.props().postConstructor((constructor, context) -> {
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(Opcodes.INVOKESTATIC, context.matcherClassName(), generatorName, GENERATOR_DESCRIPTOR, false);
            constructor.visitFieldInsn(PUTFIELD, context.matcherClassName(), fieldName, FIELD_DESCRIPTOR);
        });
    }

}
