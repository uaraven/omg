package net.ninjacat.omg.bytecode.primitive;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

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
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitInsn(POP);
        mv.visitInsn(POP2);
        mv.visitVarInsn(load(), getPropertyLocalIndex());
        mv.visitMethodInsn(INVOKESTATIC, getBoxedType(), "valueOf", getValueOfDescriptor(), false); // box property value
        mv.visitVarInsn(matchingLoad(), getMatchingLocalIndex());
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInList",
                IS_IN_LIST_DESC,
                false
        );
    }
}

