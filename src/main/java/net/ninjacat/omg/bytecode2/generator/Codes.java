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

import org.objectweb.asm.*;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;

public final class Codes {
    /**
     * Index of local var which contains matched object typecast to correct type
     */
    public static final int MATCHED_LOCAL = 2;
    public static String OBJECT_DESC = Type.getDescriptor(Object.class);
    public static Type OBJECT_TYPE = Type.getType(Object.class);
    public static String OBJECT_NAME = Type.getInternalName(Object.class);

    private Codes() {
    }


    public static void createCollectionField(final ClassVisitor cv, final String fieldName) {
        final FieldVisitor fieldVisitor = cv.visitField(
                ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC,
                fieldName,
                Type.getDescriptor(Collection.class),
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
        match.visitInsn(ICONST_0);
        match.visitJumpInsn(GOTO, end);
        match.visitLabel(notZero);
        match.visitInsn(ICONST_1);
        match.visitLabel(end);
    }
}
