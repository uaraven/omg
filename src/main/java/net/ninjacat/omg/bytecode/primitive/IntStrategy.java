package net.ninjacat.omg.bytecode.primitive;

import io.vavr.control.Try;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

public final class IntStrategy extends PrimitiveTypeStrategy {

    private static final String CONVERTER_METHOD = "getMatchingValueConverted";

    private final int compOpcode;
    private final Class<? extends PropertyPattern> basePropertyClass;

    private IntStrategy(final int compOpcode, final Class<? extends PropertyPattern> basePropertyClass) {
        this.compOpcode = compOpcode;
        this.basePropertyClass = basePropertyClass;
    }

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return basePropertyClass;
    }

    public static PatternCompilerStrategy forMethod(final ConditionMethod method, final Class propertyType) {
        final Class<? extends PropertyPattern> baseClass = selectBaseClass(propertyType);

        switch (method) {
            case EQ:
                return new IntStrategy(Opcodes.IF_ICMPEQ, baseClass);
            case NEQ:
                return new IntStrategy(Opcodes.IF_ICMPNE, baseClass);
            case LT:
                return new IntStrategy(Opcodes.IF_ICMPLT, baseClass);
            case GT:
                return new IntStrategy(Opcodes.IF_ICMPGT, baseClass);
            default:
                throw new CompilerException("Unsupported condition '%s' for int type", method);
        }
    }

    private static Class<? extends PropertyPattern> selectBaseClass(final Class propertyType) {
        return Match(propertyType).of(
                Case($(is(int.class)), i -> IntBasePropertyPattern.class),
                Case($(is(short.class)), i -> ShortBasePropertyPattern.class),
                Case($(is(byte.class)), i -> ByteBasePropertyPattern.class),
                Case($(is(char.class)), i -> CharBasePropertyPattern.class)
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
    public void convertMatchingType(final MethodVisitor match) {
        match.visitInsn(Opcodes.POP); //remove matched value
        match.visitVarInsn(Opcodes.ALOAD, 0);
        final String descriptor = Try.of(() -> Type.getMethodDescriptor(basePropertyClass.getDeclaredMethod(CONVERTER_METHOD)))
                .getOrElseThrow((ex) ->
                        new CompilerException(ex, "Failed to find converter method '%s' in class '%s",
                                CONVERTER_METHOD,
                                basePropertyClass
                        ));
        match.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(basePropertyClass), CONVERTER_METHOD, descriptor, false);
    }
}
