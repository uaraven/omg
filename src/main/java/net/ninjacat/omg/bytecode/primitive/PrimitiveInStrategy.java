package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public abstract class PrimitiveInStrategy implements PatternCompilerStrategy {
    protected static final String IS_IN_LIST_DESC = Type.getMethodDescriptor(
            Type.getType(boolean.class),
            Type.getType(List.class),
            Type.getType(Object.class));


    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return InPropertyPattern.class;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitInsn(SWAP); // swap property and list
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getBoxedType(), "valueOf", getValueOfDescriptor(), false); // box property value
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInList",
                IS_IN_LIST_DESC,
                false
        );
    }

    /**
     * @return Descriptor for valueOf() method of boxed type
     */
    protected abstract String getValueOfDescriptor();

    /**
     * @return Internal name of boxed type corresponding to this primitive
     */
    protected abstract String getBoxedType();

    @Override
    public int matchingStore() {
        return ASTORE;
    }

    @Override
    public int matchingLoad() {
        return ALOAD;
    }

    @Override
    public void beforeCompare(final MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/util/List;";
    }

}
