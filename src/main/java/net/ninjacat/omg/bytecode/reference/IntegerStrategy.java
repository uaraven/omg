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
public final class IntegerStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Integer;)I";
    private final int comparisonOpcode;

    private IntegerStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return IntegerBasePropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/lang/Integer;";
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Integer.class), COMPARE, COMPARE_DESC, false);
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new IntegerStrategy(Opcodes.IFEQ);
            case NEQ: return new IntegerStrategy(Opcodes.IFNE);
            case LT: return new IntegerStrategy(Opcodes.IFLT);
            case GT: return new IntegerStrategy(Opcodes.IFGT);
            case IN: return new ReferenceInStrategy();
            default: throw new CompilerException("Unsupported condition '%s' for Integer type", method);
        }
    }

}
