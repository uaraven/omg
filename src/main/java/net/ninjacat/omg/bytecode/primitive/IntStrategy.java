package net.ninjacat.omg.bytecode.primitive;

import io.vavr.control.Try;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class IntStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;
    private final int subtractCode;

    public IntStrategy(final int subtractCode, final int compOpcode) {
        this.compOpcode = compOpcode;
        this.subtractCode = subtractCode;
    }

    public void generateCompareCode(final MethodVisitor mv) {
        Try.of(() -> {
            final Label matched = new Label();
            final Label exit = new Label();
            mv.visitInsn(getSubtractCode());
            mv.visitJumpInsn(getCompOpcode(), matched);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitJumpInsn(Opcodes.GOTO, exit);
            mv.visitLabel(matched);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitLabel(exit);
            return null;
        }).getOrElseThrow(e -> new CompilerException(e, "Failed to generate code for Integer eq comparision"));
    }

    private int getSubtractCode() {
        return subtractCode;
    }

    private int getCompOpcode() {
        return compOpcode;
    }

    @Override
    public int store() {
        return Opcodes.ISTORE;
    }

    @Override
    public int load() {
        return Opcodes.ILOAD;
    }

    @Override
    public void convertMatchingType(final MethodVisitor match) {
        match.visitVarInsn(Opcodes.ALOAD, 0);
        match.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(IntBasePropertyPattern.class), "getMatchingValueAsInt", "()I;", false);

    }
}
