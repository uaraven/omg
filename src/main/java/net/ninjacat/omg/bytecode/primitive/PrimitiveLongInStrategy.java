package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.CompareOrdering;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class PrimitiveLongInStrategy extends PrimitiveInStrategy {

    private static final Type BOXED_LONG_TYPE = Type.getType(Long.class);
    private static final Type LONG_TYPE = Type.getType(long.class);

    @Override
    public int store() {
        return Opcodes.LSTORE;
    }

    @Override
    public int load() {
        return Opcodes.LLOAD;
    }

    @Override
    protected String getValueOfDescriptor() {
        return Type.getMethodDescriptor(BOXED_LONG_TYPE, LONG_TYPE);
    }

    @Override
    protected String getBoxedType() {
        return BOXED_LONG_TYPE.getInternalName();
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

