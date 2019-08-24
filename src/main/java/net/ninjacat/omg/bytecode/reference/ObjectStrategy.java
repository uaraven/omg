package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ObjectStrategy implements PatternCompilerStrategy {

    private final ConditionMethod conditionMethod;

    ObjectStrategy(final ConditionMethod method) {
        conditionMethod = method;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return ObjectBasePropertyPattern.class;
    }

    @Override
    public void generateCompareCode(MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(Object.class), "equals", "(Ljava/lang/Object;)Z", false);
        if (conditionMethod == ConditionMethod.NEQ) { // invert result
            final Label ifTrue = new Label();
            final Label endIf = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, ifTrue); // is false
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitJumpInsn(Opcodes.GOTO, endIf);
            mv.visitLabel(ifTrue);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitLabel(endIf);
        }
    }

    @Override
    public int store() {
        return Opcodes.ASTORE;
    }

    @Override
    public int load() {
        return Opcodes.ALOAD;
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
