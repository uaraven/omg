package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.CompareOrdering;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class PrimitiveDoubleInStrategy extends PrimitiveInStrategy {

    private static final Type BOXED_DOUBLE_TYPE = Type.getType(Double.class);
    private static final Type DOUBLE_TYPE = Type.getType(double.class);

    @Override
    public int store() {
        return Opcodes.DSTORE;
    }

    @Override
    public int load() {
        return Opcodes.DLOAD;
    }

    @Override
    protected String getValueOfDescriptor() {
        return Type.getMethodDescriptor(BOXED_DOUBLE_TYPE, DOUBLE_TYPE);
    }

    @Override
    protected String getBoxedType() {
        return BOXED_DOUBLE_TYPE.getInternalName();
    }

    @Override
    public int getMatchingLocalIndex() {
        return 4;
    }

    @Override
    public CompareOrdering compareOrdering() {
        return CompareOrdering.MATCHING_THEN_PROPERTY;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, getBoxedType(), "valueOf", getValueOfDescriptor(), false); // box property value
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInList",
                IS_IN_LIST_DESC,
                false
        );
    }
}

