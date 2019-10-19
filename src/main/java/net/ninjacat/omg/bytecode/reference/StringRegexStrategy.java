/*
 * omg: StringRegexStrategy.java
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

import net.ninjacat.omg.bytecode.CompareOrdering;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.*;

public class StringRegexStrategy implements PatternCompilerStrategy {

    private static final String METHOD_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Pattern.class));

    StringRegexStrategy() {
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return RegexBasePropertyPattern.class;
    }

    @Override
    public CompareOrdering compareOrdering() {
        return CompareOrdering.MATCHING_THEN_PROPERTY;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(INVOKEVIRTUAL,
                Type.getInternalName(java.util.regex.Pattern.class),
                "matcher", "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                Type.getInternalName(java.util.regex.Matcher.class),
                "matches", "()Z", false);
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
        return METHOD_DESCRIPTOR;
    }
}
