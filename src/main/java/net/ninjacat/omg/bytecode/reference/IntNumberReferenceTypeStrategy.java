/*
 * omg: IntNumberReferenceTypeStrategy.java
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

package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Compilation strategy for integer reference types (Long, Integer, Byte, Short, Character)
 */
public abstract class IntNumberReferenceTypeStrategy implements PatternCompilerStrategy {

    public void generateCompareCode(final MethodVisitor mv) {
        final Label success = new Label();
        final Label exit = new Label();
        callCompareTo(mv);
        mv.visitJumpInsn(getCompOpcode(), success);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, exit);
        mv.visitLabel(success);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(exit);
    }

    @Override
    public int store() {
        return Opcodes.ASTORE;
    }

    @Override
    public int load() {
        return Opcodes.ALOAD;
    }

    @Override
    public boolean isReference() {
        return true;
    }

    protected abstract int getCompOpcode();

    protected abstract void callCompareTo(MethodVisitor mv);

}
