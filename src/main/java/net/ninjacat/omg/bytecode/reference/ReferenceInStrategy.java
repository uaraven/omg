package net.ninjacat.omg.bytecode.reference;

import jdk.internal.org.objectweb.asm.Type;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;


public class ReferenceInStrategy implements PatternCompilerStrategy {
    private static final String IS_IN_LIST_DESC = Type.getMethodDescriptor(
            Type.getType(boolean.class),
            Type.getType(Object.class),
            Type.getType(List.class));

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return InPropertyPattern.class;
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(getParentPropertyPatternClass()),
                "isInList",
                IS_IN_LIST_DESC,
                false
        );
    }

    @Override
    public void beforeCompare(final MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public int store() {
        return ASTORE;
    }

    @Override
    public int load() {
        return ALOAD;
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()Ljava/util/List;";
    }

}
