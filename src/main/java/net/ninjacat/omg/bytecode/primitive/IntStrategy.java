package net.ninjacat.omg.bytecode.primitive;

import org.objectweb.asm.Type;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class IntStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;

    private IntStrategy(final int compOpcode) {
        this.compOpcode = compOpcode;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return IntBasePropertyPattern.class;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new IntStrategy(Opcodes.IFEQ);
            case NEQ: return new IntStrategy(Opcodes.IFNE);
            case LT: return new IntStrategy(Opcodes.IFLT);
            case GT: return new IntStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition '%s' for int type", method);
        }
    }

    public void generateCompareCode(final MethodVisitor mv) {
        final Label matched = new Label();
        final Label exit = new Label();
        mv.visitInsn(Opcodes.ISUB);
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
    public void convertMatchingType(final MethodVisitor match) {
        match.visitInsn(Opcodes.POP);
        match.visitVarInsn(Opcodes.ALOAD, 0);
        match.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(IntBasePropertyPattern.class), "getMatchingValueAsInt", "()I", false);
    }
}
