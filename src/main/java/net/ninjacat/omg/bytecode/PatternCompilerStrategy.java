package net.ninjacat.omg.bytecode;


import org.objectweb.asm.MethodVisitor;

public interface PatternCompilerStrategy {

    /**
     * Generates code to read property.
     *
     * At the moment of call, instance reference will be on JVM stack
     * @param mv {@link MethodVisitor} to generate code
     * @param property
     */
    void generatePropertyGet(MethodVisitor mv, Property property);

    /**
     * Should generate code to compare two objects
     *
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
     * @return
     */
    int store();

    /**
     * Opcode for loading property value from local variable to stack
     * @return
     */
    int load();

    /**
     * returns true if property type is reference
     * @return
     */
    boolean isReference();

    /**
     * Generate code to possibly unbox matching value for comparison
     * @param match
     */
    void convertMatchingType(MethodVisitor match);
}
