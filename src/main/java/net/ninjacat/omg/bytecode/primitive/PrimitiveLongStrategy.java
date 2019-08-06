package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class PrimitiveLongStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;

    private PrimitiveLongStrategy(final int compOpcode) {
        this.compOpcode = compOpcode;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return LongBasePropertyPattern.class;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ:
                return new PrimitiveLongStrategy(Opcodes.IFEQ);
            case NEQ:
                return new PrimitiveLongStrategy(Opcodes.IFNE);
            case LT:
                return new PrimitiveLongStrategy(Opcodes.IFLT);
            case GT:
                return new PrimitiveLongStrategy(Opcodes.IFGT);
            case IN:
                return new PrimitiveLongInStrategy();
            default:
                throw new CompilerException("Unsupported condition '%s' for int type", method);
        }
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()" + Type.getDescriptor(long.class);
    }


    public void generateCompareCode(final MethodVisitor mv) {
        final Label matched = new Label();
        final Label exit = new Label();
        mv.visitInsn(Opcodes.LCMP);
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
        return Opcodes.LSTORE;
    }

    @Override
    public int load() {
        return Opcodes.LLOAD;
    }

    @Override
    public int getMatchingLocalIndex() {
        return 4;
    }

}
