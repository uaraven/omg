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
public final class FloatStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Float;)I";
    private final int comparisonOpcode;

    private FloatStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Float.class), COMPARE, COMPARE_DESC, false);
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return FloatBasePropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/lang/Float;";
    }


    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new FloatStrategy(Opcodes.IFEQ);
            case NEQ: return new FloatStrategy(Opcodes.IFNE);
            case LT: return new FloatStrategy(Opcodes.IFLT);
            case GT: return new FloatStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition '%s' for Integer type", method);
        }
    }

}
