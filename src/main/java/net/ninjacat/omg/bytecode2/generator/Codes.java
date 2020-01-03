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

import io.vavr.control.Try;
import org.objectweb.asm.*;

import java.util.Random;

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


    public static void pushLong(final MethodVisitor mv, final long val) {
        if (val == 0) {
            mv.visitInsn(LCONST_0);
        } else if (val == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(val);
        }
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
     * Reads string as either Long or Double and returns it as {@link Number}
     *
     * @param numberRepr String containing numeric literal
     * @return Number
     */
    public static Number strToNumber(final String numberRepr) {
        return Try.of(() -> (Number) Long.parseLong(numberRepr)).getOrElseTry(() -> Double.parseDouble(numberRepr));
    }

}
