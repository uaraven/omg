package net.ninjacat.omg.bytecode.primitive;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class PrimitiveIntInStrategy extends PrimitiveInStrategy {

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
        return Type.getMethodDescriptor(Type.getType(Integer.class), Type.getType(int.class));
    }

    @Override
    protected String getBoxedType() {
        return Type.getInternalName(Integer.class);
    }
}
