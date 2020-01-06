/*
 * omg: ObjectEqCodeGenerator.java
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

import static io.vavr.API.*;
import static java.util.function.Predicate.isEqual;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * Generates code to perform equalTo/notEqualTo comparision on objects. Relies on properly implemented
 * {@link Object#equals(Object)} method
 *
 * @param <T>
 * @param <P>
 */
@SuppressWarnings("rawtypes")
public class ComparableCodeGenerator<T, P extends Comparable> implements TypedCodeGenerator<T, P, P> {
    private static final String JAVA_LANG_COMPARABLE = "java/lang/Comparable";
    private static final String COMPARE_TO = "compareTo";
    private static final String COMPARE_TO_DESC = "(Ljava/lang/Object;)I";
    private final CodeGenerationContext context;

    public ComparableCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void generateHelpers(final Property<T, P> property, final PropertyCondition<P> condition) {
        context.props().prop("class", property.getType());
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<P> condition, final MethodVisitor method) {
        final Class propClass = context.props().get("class", Class.class);
        method.visitLdcInsn(condition.getValue());
        method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(propClass), "valueOf",
                Codes.getMethodDescriptor(propClass, Codes.unboxedType(propClass)), false);
    }

    @Override
    public void compare(final PropertyCondition<P> condition, final MethodVisitor method) {
        method.visitMethodInsn(INVOKEINTERFACE, JAVA_LANG_COMPARABLE, COMPARE_TO, COMPARE_TO_DESC, true);
        final int opcode = Match(condition.getMethod()).of(
                Case($(isEqual(ConditionMethod.EQ)), r -> Opcodes.IFEQ),
                Case($(isEqual(ConditionMethod.NEQ)), r -> Opcodes.IFNE),
                Case($(isEqual(ConditionMethod.LT)), r -> Opcodes.IFGT),
                Case($(isEqual(ConditionMethod.GT)), r -> Opcodes.IFLT),
                Case($(), x -> {
                    throw new CompilerException("Unsupported Condition for Comparable type: %s", condition);
                })
        );
        Codes.compareWithOpcode(method, opcode);
    }
}
