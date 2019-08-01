package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Compilation strategy for java.lang.Integer type
 */
public final class ShortStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Short;)I";
    private final int comparisonOpcode;

    private ShortStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Short.class), COMPARE, COMPARE_DESC, false);
    }

    @Override
    public void convertMatchingType(final MethodVisitor match) {
        match.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Short.class));
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new ShortStrategy(Opcodes.IFEQ);
            case NEQ: return new ShortStrategy(Opcodes.IFNE);
            case LT: return new ShortStrategy(Opcodes.IFLT);
            case GT: return new ShortStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition '%s' for Short type", method);
        }
    }

}
