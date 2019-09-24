package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class EnumStrategy implements PatternCompilerStrategy {

    private final ConditionMethod method;

    private EnumStrategy(final ConditionMethod method) {
        this.method = method;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ:
            case NEQ:
                return new EnumStrategy(method);
            case IN:
                return new ReferenceInStrategy();
            case REGEX:
                return new ObjectRegexStrategy();
            default:
                throw new CompilerException("Unsupported condition '%s' for Enum type", method);
        }
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return EnumBasePropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/lang/Enum;";
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        final Label equal = new Label();
        final Label ifEnd = new Label();
        mv.visitJumpInsn(method == ConditionMethod.EQ
                ? Opcodes.IF_ACMPEQ
                : Opcodes.IF_ACMPNE, equal);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, ifEnd);
        mv.visitLabel(equal);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(ifEnd);
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
