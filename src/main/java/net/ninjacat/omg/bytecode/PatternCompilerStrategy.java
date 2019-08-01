package net.ninjacat.omg.bytecode;


import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface PatternCompilerStrategy {

    /**
     * Returns class to be used as parent for generated {@link PropertyPattern}.
     * <p>
     * Class must have the same constructor as {@link BasePropertyPattern}
     *
     * @return Class to be used as parent for generated property pattern.
     */
    default Class<? extends PropertyPattern> getParentPropertyPatternClass() {
        return BasePropertyPattern.class;
    }

    /**
     * Generates code to read property.
     * <p>
     * At the moment of call, instance reference will be on JVM stack. Generated code must leave property value on stack.
     *
     * @param mv       {@link MethodVisitor} to generate code
     * @param property
     */
    @SuppressWarnings("FeatureEnvy")
    default void generatePropertyGet(final MethodVisitor mv, final Property property) {
        final String internalName = Type.getInternalName(property.getOwner());
        mv.visitTypeInsn(Opcodes.CHECKCAST, internalName);
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                internalName,
                property.getMethod().getName(),
                property.getMethod().getDescriptor(),
                false);
    }

    /**
     * Should generate code to compare two objects
     * <p>
     * At the moment of call, JVM stack contains:
     * <pre>
     *     - matching value from pattern (unboxed in case of primitive)
     *     - actual property value from instance being matched
     * </pre>
     * After the call stack must contain either ICONST_0 or ICONST_1, i.e. {@code false} or {@code true}
     *
     * @param mv {@link MethodVisitor} to generate code
     */
    void generateCompareCode(MethodVisitor mv);

    /**
     * Opcode for storing property value from stack to local variable
     *
     * @return
     */
    int store();

    /**
     * Opcode for loading property value from local variable to stack
     *
     * @return
     */
    int load();

    /**
     * returns true if property type is reference
     *
     * @return
     */
    boolean isReference();

    /**
     * Generate code to possibly unbox matching value for comparison
     *
     * @param match
     */
    void convertMatchingType(MethodVisitor match);

    /**
     * Returns method descriptor for {@link BasePropertyPattern#getMatchingValue()}
     *
     * Default value is ""()Ljava/lang/Object;" and rarely has to be changed
     *
     * @return
     */
    default String getMatchingValueDescriptor() {
        return "()Ljava/lang/Object;";
    }
}
