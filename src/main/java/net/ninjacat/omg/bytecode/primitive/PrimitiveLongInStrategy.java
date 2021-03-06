/*
 * omg: PrimitiveLongInStrategy.java
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

import net.ninjacat.omg.bytecode.CompareOrdering;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class PrimitiveLongInStrategy extends PrimitiveInStrategy {

    private static final Type BOXED_LONG_TYPE = Type.getType(Long.class);
    private static final Type LONG_TYPE = Type.getType(long.class);

    @Override
    public int store() {
        return Opcodes.LSTORE;
    }

    @Override
    public int load() {
        return Opcodes.LLOAD;
    }

    @Override
    protected String getValueOfDescriptor() {
        return Type.getMethodDescriptor(BOXED_LONG_TYPE, LONG_TYPE);
    }

    @Override
    protected String getBoxedType() {
        return BOXED_LONG_TYPE.getInternalName();
    }

    @Override
    public int getMatchingLocalIndex() {
        return 4;
    }

    @Override
    public CompareOrdering compareOrdering() {
        return CompareOrdering.MATCHING_THEN_PROPERTY;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, getBoxedType(), "valueOf", getValueOfDescriptor(), false); // box property value
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                METHOD,
                IS_IN_COLLECTION_DESC,
                false
        );
    }
}

