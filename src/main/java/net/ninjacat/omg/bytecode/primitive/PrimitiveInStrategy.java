/*
 * omg: PrimitiveInStrategy.java
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
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public abstract class PrimitiveInStrategy implements PatternCompilerStrategy {
    protected static final String IS_IN_LIST_DESC = Type.getMethodDescriptor(
            Type.getType(boolean.class),
            Type.getType(List.class),
            Type.getType(Object.class));


    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return InPropertyPattern.class;
    }

    @Override
    public CompareOrdering compareOrdering() {
        return CompareOrdering.MATCHING_THEN_PROPERTY;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getBoxedType(), "valueOf", getValueOfDescriptor(), false); // box property value
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInList",
                IS_IN_LIST_DESC,
                false
        );
    }

    /**
     * @return Descriptor for valueOf() method of boxed type
     */
    protected abstract String getValueOfDescriptor();

    /**
     * @return Internal name of boxed type corresponding to this primitive
     */
    protected abstract String getBoxedType();

    @Override
    public int matchingStore() {
        return ASTORE;
    }

    @Override
    public int matchingLoad() {
        return ALOAD;
    }

    @Override
    public void beforeCompare(final MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/util/List;";
    }

}
