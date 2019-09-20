package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.CompareOrdering;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class ObjectRegexStrategy implements PatternCompilerStrategy {

    private static final String METHOD_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Pattern.class));

    ObjectRegexStrategy() {
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return RegexBasePropertyPattern.class;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(Object.class), "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                Type.getInternalName(java.util.regex.Pattern.class),
                "matcher", "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
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
    public CompareOrdering compareOrdering() {
        return CompareOrdering.MATCHING_THEN_PROPERTY;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return METHOD_DESCRIPTOR;
    }
}
