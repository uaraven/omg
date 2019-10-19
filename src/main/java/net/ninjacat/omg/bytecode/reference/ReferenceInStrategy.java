/*
 * omg: ReferenceInStrategy.java
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
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Collection;

import static org.objectweb.asm.Opcodes.*;


public class ReferenceInStrategy implements PatternCompilerStrategy {
    private static final String IS_IN_COLLECTION_DESC = Type.getMethodDescriptor(
            Type.getType(boolean.class),
            Type.getType(Object.class),
            Type.getType(Collection.class));

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return InPropertyPattern.class;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInCollection",
                IS_IN_COLLECTION_DESC,
                false
        );
    }

    @Override
    public void beforeCompare(final MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public int store() {
        return ASTORE;
    }

    @Override
    public int load() {
        return ALOAD;
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return Type.getMethodDescriptor(Type.getType(Collection.class));
    }

}
