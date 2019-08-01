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
public final class CharacterStrategy extends IntNumberReferenceTypeStrategy {

    private static final String COMPARE = "compareTo";
    private static final String COMPARE_DESC = "(Ljava/lang/Character;)I";
    private final int comparisonOpcode;

    private CharacterStrategy(final int comparisonOpcode) {
        this.comparisonOpcode = comparisonOpcode;
    }

    @Override
    protected int getCompOpcode() {
        return comparisonOpcode;
    }

    @Override
    protected void callCompareTo(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Character.class), COMPARE, COMPARE_DESC, false);
    }

    @Override
    public void convertMatchingType(final MethodVisitor match) {
        match.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Character.class));
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ: return new CharacterStrategy(Opcodes.IFEQ);
            case NEQ: return new CharacterStrategy(Opcodes.IFNE);
            case LT: return new CharacterStrategy(Opcodes.IFLT);
            case GT: return new CharacterStrategy(Opcodes.IFGT);
            default: throw new CompilerException("Unsupported condition for Integer type: '%s'", method);
        }
    }

}
