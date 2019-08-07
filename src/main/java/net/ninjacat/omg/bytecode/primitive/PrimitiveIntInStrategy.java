package net.ninjacat.omg.bytecode.primitive;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class PrimitiveIntInStrategy extends PrimitiveInStrategy {

    private static final Type BOXED_TYPE = Type.getType(Integer.class);
    private static final Type TYPE = Type.getType(int.class);

    @Override
    public int store() {
        return Opcodes.ISTORE;
    }

    @Override
    public int load() {
        return Opcodes.ILOAD;
    }

    @Override
    protected String getValueOfDescriptor() {
        return Type.getMethodDescriptor(BOXED_TYPE, TYPE);
    }

    @Override
    protected String getBoxedType() {
        return BOXED_TYPE.getInternalName();
    }
}
