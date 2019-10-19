/*
 * omg: StringStrategy.java
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
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class StringStrategy implements PatternCompilerStrategy {

    private final ConditionMethod conditionMethod;

    StringStrategy(final ConditionMethod method) {
        conditionMethod = method;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(String.class), "equals", "(Ljava/lang/Object;)Z", false);
        if (conditionMethod == ConditionMethod.NEQ) { // invert result
            final Label ifTrue = new Label();
            final Label endIf = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, ifTrue); // is false
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitJumpInsn(Opcodes.GOTO, endIf);
            mv.visitLabel(ifTrue);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitLabel(endIf);
        }
    }


    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return StringBasePropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/lang/String;";
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

}
