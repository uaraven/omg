package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ObjectMatchStrategy implements PatternCompilerStrategy {

    @Override
    public Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return BaseObjectMatchPropertyPattern.class;
    }

    @Override
    public String getMatchingValueDescriptor() {
        return "()" + Type.getDescriptor(Pattern.class);
    }

    @Override
    public void generateCompareCode(final MethodVisitor mv) {
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(
                INVOKEINTERFACE,
                Type.getInternalName(Pattern.class),
                "matches",
                "(Ljava/lang/Object;)Z",
                true
        );
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

}
