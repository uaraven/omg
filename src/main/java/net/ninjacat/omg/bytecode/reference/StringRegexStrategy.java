package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.regex.Pattern;

import static net.ninjacat.omg.conditions.ConditionMethod.REGEX;

public class StringRegexStrategy implements PatternCompilerStrategy {

    StringRegexStrategy() {
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return BaseRegexPropertyPattern.class;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitInsn(Opcodes.SWAP); // pattern should be first on stack
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(java.util.regex.Pattern.class),
                "matcher", "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(java.util.regex.Matcher.class),
                "matches", "()Z", false);
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

    @Override
    public void convertMatchingType(final MethodVisitor match) {
        // TODO: remove if unused after primitive types are implemented
    }

    @Override
    public String getMatchingValueDescriptor() {
        return Type.getMethodDescriptor(Type.getType(Pattern.class));
    }
}
