package net.ninjacat.omg.bytecode;

import io.vavr.control.Try;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.apache.commons.lang3.ClassUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;

/**
 * Generates and loads class implementing {@link PropertyPattern<T>}
 *
 * @param <T> Type of object to match property on
 */
class PropertyPatternGenerator<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class),
            Type.getType(Property.class), Type.getType(Object.class));
    private static final String MATCHES_DESC = Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class));
    private static final String PACKAGE_NAME = PropertyPatternGenerator.class.getPackage() + ".generated";
    private static final String PACKAGE_DESC = PACKAGE_NAME.replaceAll("\\.", "/");

    private final Property<T> property;
    private final PropertyCondition condition;
    private final PatternCompilerStrategy compGen;

    PropertyPatternGenerator(final Property<T> property, final PropertyCondition condition, final PatternCompilerStrategy strategy) {
        this.property = property;
        this.condition = condition;
        this.compGen = strategy;
    }

    @SuppressWarnings("unchecked")
    PropertyPattern<T> compilePattern() {
        final ClassReader classReader =
                Try.of(() -> new ClassReader(compGen.getParentPropertyPatternClass().getName()))
                        .getOrElseThrow(ex -> new CompilerException(ex, "Failed to read base class for compiling"));
        final ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        final Type type = Type.getType(compGen.getParentPropertyPatternClass());
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, generateClassName(), null, type.getInternalName(), null);
        createConstructor(writer);
        createMatches(writer);

        writer.visitEnd();
        return Try.of(() -> {
            CompileDebugger.dumpClass("/tmp/Generated.class", writer.toByteArray());
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(), writer.toByteArray());
            final Class propType = property.getType().isPrimitive()
                    ? ClassUtils.primitiveToWrapper(property.getType())
                    : property.getType();
            final Constructor<?> constructor = patternClass.getConstructor(Property.class, propType);
            return (PropertyPattern<T>) constructor.newInstance(property, condition.getValue());
        }).getOrElseThrow((ex) -> new CompilerException(ex, "Failed to generate accessor for '%s'", property));
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv {@link ClassVisitor} for which constructor will be generated
     */
    private void createConstructor(final ClassVisitor cv) {
        final Class wrapperType = ClassUtils.primitiveToWrapper(property.getType());
        final String initDescriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(Property.class), Type.getType(wrapperType));
        final MethodVisitor init = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", initDescriptor, null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitVarInsn(Opcodes.ALOAD, 1);
        init.visitVarInsn(Opcodes.ALOAD, 2);
        init.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                Type.getInternalName(compGen.getParentPropertyPatternClass()),
                "<init>",
                BASE_INIT_DESCRIPTOR,
                false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
    }

    /**
     * Generates .matches() implementation.
     * <p>
     * Implementation reads property from passed object instance, performs null checks and then calls provided strategy
     * to generate casting and comparison code
     */
    @SuppressWarnings({"FeatureEnvy", "OverlyLongMethod"})
    private void createMatches(final ClassVisitor cv) {

        final MethodVisitor match = cv.visitMethod(Opcodes.ACC_PUBLIC, "matches", MATCHES_DESC, "(TT;)Z", null);
        final Label propNotNull = new Label();
        final Label matchingNotNull = new Label();
        final Label matchingNotNull2 = new Label();
        final Label start = new Label();
        final Label end = new Label();

        final Label localMatchingStart = new Label();
        final Label localMatchingEnd = new Label();
        final Label localPropStart = new Label();
        final Label localPropEnd = new Label();

        final int localProperty = 2;
        final int localMatching = 3;

        match.visitCode();

        // get matching value and store it to local var
        match.visitLabel(start);
        match.visitVarInsn(Opcodes.ALOAD, 0); // this
        match.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(compGen.getParentPropertyPatternClass())
                , "getMatchingValue", compGen.getMatchingValueDescriptor(), false);
        match.visitLabel(localMatchingStart);
        match.visitVarInsn(Opcodes.ASTORE, localMatching);

        // get property from instance
        match.visitVarInsn(Opcodes.ALOAD, 1); // instance parameter
        compGen.generatePropertyGet(match, property);
        match.visitLabel(localPropStart);
        match.visitVarInsn(compGen.store(), localProperty);
        // if property type is reference then perform reference checks
        if (compGen.isReference()) {
            // if (property != null) goto comparision
            match.visitVarInsn(compGen.load(), localProperty);
            match.visitJumpInsn(Opcodes.IFNONNULL, propNotNull);

            // property == null, now if matching value == null return true
            match.visitVarInsn(Opcodes.ALOAD, localMatching);
            match.visitJumpInsn(Opcodes.IFNONNULL, matchingNotNull);

            match.visitInsn(Opcodes.ICONST_1);
            match.visitInsn(Opcodes.IRETURN);
            // matching is not null, return false
            match.visitLabel(matchingNotNull);
            match.visitInsn(Opcodes.ICONST_0);
            match.visitInsn(Opcodes.IRETURN);

            match.visitLabel(propNotNull);
        }
        // at this point if matching value is null, return false
        match.visitVarInsn(Opcodes.ALOAD, localMatching);
        match.visitJumpInsn(Opcodes.IFNONNULL, matchingNotNull2);
        // if it is null, return false
        match.visitInsn(Opcodes.ICONST_0);
        match.visitInsn(Opcodes.IRETURN);

        match.visitLabel(matchingNotNull2);
        match.visitVarInsn(compGen.load(), localProperty);

        match.visitVarInsn(Opcodes.ALOAD, localMatching);
        compGen.convertMatchingType(match);
        match.visitLabel(localMatchingEnd);
        match.visitLabel(localPropEnd);

        compGen.generateCompareCode(match);
        match.visitInsn(Opcodes.IRETURN);
        match.visitLabel(end);

        match.visitMaxs(0, 0);
        match.visitLocalVariable("this", "L" + generateClassName() + ";", null, start, end, 0);
        match.visitLocalVariable("instance", Type.getDescriptor(Object.class), null, start, end, 1);
        match.visitLocalVariable("localProperty", Type.getDescriptor(property.getType()), null, localPropStart, localPropEnd, localProperty);
        match.visitLocalVariable("localMatching", Type.getDescriptor(Object.class), null, localMatchingStart, localMatchingEnd, localMatching);

        match.visitEnd();
    }

    private String generateClassName() {
        return PACKAGE_DESC + "/Gen" + property.getOwner().getSimpleName() + '$' +
                property.getPropertyName() + '$' + "PropertyPattern";
    }

    private String generateBinaryClassName() {
        return PACKAGE_NAME + ".Gen" + property.getOwner().getSimpleName() + '$' +
                property.getPropertyName() + '$' + "PropertyPattern";
    }

}
