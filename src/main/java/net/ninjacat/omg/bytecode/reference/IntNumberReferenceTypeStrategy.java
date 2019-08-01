package net.ninjacat.omg.bytecode.reference;

import io.vavr.control.Try;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Compilation strategy for integer reference types (Long, Integer, Byte, Short, Character)
 */
public abstract class IntNumberReferenceTypeStrategy implements PatternCompilerStrategy {
    
    @SuppressWarnings("FeatureEnvy")
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

    public void generateCompareCode(final MethodVisitor mv) {
        Try.of(() -> {
            final Label success = new Label();
            final Label exit = new Label();
            callCompareTo(mv);
            mv.visitJumpInsn(getCompOpcode(), success);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitJumpInsn(Opcodes.GOTO, exit);
            mv.visitLabel(success);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitLabel(exit);
            return null;
        }).getOrElseThrow(e -> new CompilerException(e, "Failed to generate code for Integer eq comparision"));
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
        // no-op
    }

    protected abstract int getCompOpcode();

    protected abstract void callCompareTo(MethodVisitor mv);

}
