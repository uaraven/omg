package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Compilation strategy for java.lang.Long type
 */
public class LongStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Long;)I";
    private final int comparisonOpcode;

    private LongStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getDescriptor(Long.class), COMPARE, COMPARE_DESC, false);
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new LongStrategy(Opcodes.IFEQ);
            case NEQ: return new LongStrategy(Opcodes.IFNE);
            case LT: return new LongStrategy(Opcodes.IFLT);
            case GT: return new LongStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition for Long type: '%s'", method);
        }
    }
}
