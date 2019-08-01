package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class StringStrategy implements PatternCompilerStrategy {

    private final ConditionMethod conditionMethod;

    public StringStrategy(final ConditionMethod method) {
        conditionMethod = method;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ:
            case NEQ:
                return new StringStrategy(method);
            default: throw new CompilerException("Unsupported condition for String type: '%s'", method);
        }
    }

    @Override
    public void generatePropertyGet(final MethodVisitor mv, final Property property) {
        final String internalName = Type.getInternalName(property.getOwner());
        mv.visitTypeInsn(Opcodes.CHECKCAST, internalName);
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                internalName,
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                false);

    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(String.class), "equals", "(Ljava/lang/Object;)Z", false);
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

    @Override
    public void convertMatchingType(final MethodVisitor match) {

    }
}
