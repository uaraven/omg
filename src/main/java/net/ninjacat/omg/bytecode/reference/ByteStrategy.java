package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Compilation strategy for java.lang.Integer type
 */
public final class ByteStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Byte;)I";
    private final int comparisonOpcode;

    private ByteStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Byte.class), COMPARE, COMPARE_DESC, false);
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return ByteBasePropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/lang/Byte;";
    }


    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new ByteStrategy(Opcodes.IFEQ);
            case NEQ: return new ByteStrategy(Opcodes.IFNE);
            case LT: return new ByteStrategy(Opcodes.IFLT);
            case GT: return new ByteStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition '%s' for Byte type", method);
        }
    }

}
