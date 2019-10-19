/*
 * omg: PrimitiveDoubleStrategy.java
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

package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class PrimitiveDoubleStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;

    private PrimitiveDoubleStrategy(final int compOpcode) {
        this.compOpcode = compOpcode;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return DoubleBasePropertyPattern.class;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new PrimitiveDoubleStrategy(Opcodes.IFEQ);
            case NEQ: return new PrimitiveDoubleStrategy(Opcodes.IFNE);
            case LT: return new PrimitiveDoubleStrategy(Opcodes.IFLT);
            case GT: return new PrimitiveDoubleStrategy(Opcodes.IFGT);
            case IN:
                return new PrimitiveDoubleInStrategy();
            default:
                throw new CompilerException("Unsupported condition '%s' for 'double' type", method);
        }
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()" + Type.getDescriptor(double.class);
    }

    public void generateCompareCode(final MethodVisitor mv) {
        final Label matched = new Label();
        final Label exit = new Label();
        mv.visitInsn(Opcodes.DCMPG);
        mv.visitJumpInsn(getCompOpcode(), matched);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, exit);
        mv.visitLabel(matched);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(exit);
    }

    private int getCompOpcode() {
        return compOpcode;
    }

    @Override
    public int store() {
        return Opcodes.DSTORE;
    }

    @Override
    public int load() {
        return Opcodes.DLOAD;
    }

    @Override
    public int getMatchingLocalIndex() {
        return 4;
    }

}
