/*
 * omg: Codes.java
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

import io.vavr.control.Option;
import io.vavr.control.Try;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.Random;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;
import static org.objectweb.asm.Opcodes.*;

public final class Codes {
    /**
     * Index of local var which contains matched object typecast to correct type
     */
    public static final int MATCHED_LOCAL = 2;
    public static final String OBJECT_DESC = Type.getDescriptor(Object.class);
    public static final Type OBJECT_TYPE = Type.getType(Object.class);
    public static final String OBJECT_NAME = Type.getInternalName(Object.class);

    public static final Random RNDG = new Random();

    private Codes() {
    }


    public static <T> void createField(final ClassVisitor cv,
                                       final String fieldName,
                                       final Class<T> fieldClass) {
        final FieldVisitor fieldVisitor = cv.visitField(
                ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC,
                fieldName,
                Type.getDescriptor(fieldClass),
                null, null);
        fieldVisitor.visitEnd();
    }

    public static void pushInt(final MethodVisitor mv, final int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }


    public static void pushDouble(final MethodVisitor mv, final double val) {
        if (val == 0) {
            mv.visitInsn(DCONST_0);
        } else if (val == 1) {
            mv.visitInsn(DCONST_1);
        } else {
            mv.visitLdcInsn(val);
        }
    }

    public static void pushFloat(final MethodVisitor mv, final float val) {
        if (val == 0) {
            mv.visitInsn(FCONST_0);
        } else if (val == 1) {
            mv.visitInsn(FCONST_1);
        } else if (val == 2) {
            mv.visitInsn(FCONST_2);
        } else {
            mv.visitLdcInsn(val);
        }
    }

    public static void pushLong(final MethodVisitor mv, final long val) {
        if (val == 0) {
            mv.visitInsn(LCONST_0);
        } else if (val == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(val);
        }
    }

    public static void pushBoolean(final MethodVisitor mv, final boolean value) {
        pushInt(mv, value ? 1 : 0);
    }

    /**
     * Pushes enum instance to the stack.
     * <p>
     * Gets static field of given enum value of given enum type and pushes it to the stack. If enum class does not have
     * a value with provided name loads {@link NeverMatchingEnum#INSTANCE} instead which should not match any
     * of known enum values.
     *
     * @param mv        {@link MethodVisitor}
     * @param enumClass Enum class
     * @param enumName  Name of the enum constant
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void pushEnum(final MethodVisitor mv, final Class<Enum> enumClass, final String enumName) {
        final Enum<?> enumConst = Try.of(() -> Enum.valueOf(enumClass, enumName)).getOrElse(NeverMatchingEnum.INSTANCE);
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                Type.getInternalName(enumConst.getClass()),
                enumConst.name(),
                Type.getDescriptor(enumConst.getClass()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void pushEnumIfExist(final MethodVisitor mv, final Class<Enum> enumClass, final String enumName) {
        final Option<Enum> enumConst = Try.of(() -> Enum.valueOf(enumClass, enumName)).toOption();
        enumConst.forEach(e -> {
            mv.visitFieldInsn(Opcodes.GETSTATIC,
                    Type.getInternalName(enumConst.getClass()),
                    e.name(),
                    Type.getDescriptor(enumConst.getClass()));
        });
    }

    /**
     * Performs logical NOT on the integer value on the stack.
     * <p>
     * If value on the stack is zero, then it will be replaced with 1, otherwise it will be replaced with zero
     *
     * @param match {@link MethodVisitor}
     */
    public static void logicalNot(final MethodVisitor match) {
        final Label notZero = new Label();
        final Label end = new Label();
        match.visitJumpInsn(IFNE, notZero);
        match.visitInsn(ICONST_1);
        match.visitJumpInsn(GOTO, end);
        match.visitLabel(notZero);
        match.visitInsn(ICONST_0);
        match.visitLabel(end);
    }

    /**
     * Based on execution of Opcode (one of IFEQ, IFNE, IFLT, or IFGT)
     * puts either 0 or 1 on stack. If opcode is "true" then stack will contain 1
     *
     * @param method {@link MethodVisitor}
     * @param opcode Comparision opcode
     */
    public static void compareWithOpcode(final MethodVisitor method, final int opcode) {
        final Label trueLbl = new Label();
        final Label finalLbl = new Label();
        method.visitJumpInsn(opcode, trueLbl);
        method.visitInsn(ICONST_0);
        method.visitJumpInsn(GOTO, finalLbl);
        method.visitLabel(trueLbl);
        method.visitInsn(ICONST_1);
        method.visitLabel(finalLbl);
    }

    /**
     * Reads string as either Long or Double and returns it as {@link Number}
     *
     * @param numberRepr String containing numeric literal
     * @return Number
     */
    public static Number strToNumber(final String numberRepr) {
        return Try.of(() -> (Number) Long.parseLong(numberRepr)).getOrElseTry(() -> Double.parseDouble(numberRepr));
    }

    public static String getMethodDescriptor(final Class<?> returnClass, final Class<?>... parameterClasses) {
        final Type returnType = Type.getType(returnClass);
        final Type[] paramTypes = Arrays.stream(parameterClasses).map(Type::getType).toArray(Type[]::new);
        return Type.getMethodDescriptor(returnType, paramTypes);
    }

    @SuppressWarnings("rawtypes")
    public static Class<?> unboxedType(final Class boxed) {
        return Match(boxed).of(
                Case($(is(Integer.class)), c -> int.class),
                Case($(is(Byte.class)), c -> byte.class),
                Case($(is(Short.class)), c -> short.class),
                Case($(is(Character.class)), c -> char.class),
                Case($(is(Long.class)), c -> long.class),
                Case($(is(Float.class)), c -> float.class),
                Case($(is(Double.class)), c -> double.class),
                Case($(is(Boolean.class)), c -> boolean.class),
                Case($(), c -> {
                            throw new CompilerException("Class '%s' cannot be unboxed", boxed);
                        }
                )
        );
    }

    public static boolean isComparableNumber(final Class<?> propClass) {
        return Number.class.isAssignableFrom(propClass) && Comparable.class.isAssignableFrom(propClass);
    }


}
