/*
 * omg: IntStrategy.java
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

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

public final class IntStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;
    private final String propertyTypeDescriptor;
    private final Class<? extends PropertyPattern> basePropertyClass;

    private IntStrategy(final int compOpcode,
                        final String propertyTypeDescriptor,
                        final Class<? extends PropertyPattern> basePropertyClass) {
        this.compOpcode = compOpcode;
        this.propertyTypeDescriptor = propertyTypeDescriptor;
        this.basePropertyClass = basePropertyClass;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return basePropertyClass;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method, final Class propertyType) {
        final Class<? extends PropertyPattern> baseClass = selectBaseClass(propertyType);

        return Match(method).of(
                Case($(is(ConditionMethod.EQ)), i -> new IntStrategy(Opcodes.IF_ICMPEQ, Type.getDescriptor(propertyType), baseClass)),
                Case($(is(ConditionMethod.NEQ)), i -> new IntStrategy(Opcodes.IF_ICMPNE, Type.getDescriptor(propertyType), baseClass)),
                Case($(is(ConditionMethod.LT)), i -> new IntStrategy(Opcodes.IF_ICMPLT, Type.getDescriptor(propertyType), baseClass)),
                Case($(is(ConditionMethod.GT)), i -> new IntStrategy(Opcodes.IF_ICMPGT, Type.getDescriptor(propertyType), baseClass)),
                Case($(is(ConditionMethod.IN)), i -> selectInStrategy(propertyType)),
                Case($(), () -> {
                    throw new CompilerException("Unsupported condition '%s' for '%s' type", method, propertyType.getName());
                })
        );
    }

    private static PrimitiveInStrategy selectInStrategy(final Class propertyType) {
        return Match(propertyType).of(
                Case($(is(int.class)), i -> new PrimitiveIntInStrategy()),
                Case($(is(byte.class)), i -> new PrimitiveByteInStrategy()),
                Case($(is(short.class)), i -> new PrimitiveShortInStrategy()),
                Case($(is(char.class)), i -> new PrimitiveCharInStrategy())
        );
    }

    private static Class<? extends PropertyPattern> selectBaseClass(final Class propertyType) {
        return Match(propertyType).of(
                Case($(is(int.class)), i -> IntBasePropertyPattern.class),
                Case($(is(short.class)), i -> ShortBasePropertyPattern.class),
                Case($(is(byte.class)), i -> ByteBasePropertyPattern.class),
                Case($(is(char.class)), i -> CharBasePropertyPattern.class)
        );
    }

    public void generateCompareCode(final MethodVisitor mv) {
        final Label matched = new Label();
        final Label exit = new Label();
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
        return Opcodes.ISTORE;
    }

    @Override
    public int load() {
        return Opcodes.ILOAD;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()" + propertyTypeDescriptor;
    }

}
