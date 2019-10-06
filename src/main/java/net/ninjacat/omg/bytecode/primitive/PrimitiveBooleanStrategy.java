package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

public final class PrimitiveBooleanStrategy extends PrimitiveTypeStrategy {

    private final int compOpcode;

    private PrimitiveBooleanStrategy(final int compOpcode) {
        this.compOpcode = compOpcode;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return BooleanBasePropertyPattern.class;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        return Match(method).of(
                Case($(is(ConditionMethod.EQ)), i -> new PrimitiveBooleanStrategy(Opcodes.IF_ICMPEQ)),
                Case($(is(ConditionMethod.NEQ)), i -> new PrimitiveBooleanStrategy(Opcodes.IF_ICMPNE)),
                Case($(), () -> {
                    throw new CompilerException("Unsupported condition '%s' for '%s' type", method, "boolean");
                })
        );
    }

    public void generateCompareCode(final MethodVisitor mv) {
        final Label matched = new Label();
        final Label exit = new Label();
        mv.visitJumpInsn(getCompOpcode(), matched);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, exit);
        mv.visitLabel(matched);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(exit);
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
    public String getMatchingValueDescriptor() {
        return "()Z";
    }

}
