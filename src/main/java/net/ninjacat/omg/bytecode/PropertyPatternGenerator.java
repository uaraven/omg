package net.ninjacat.omg.bytecode;

import io.vavr.control.Try;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.apache.commons.lang3.ClassUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates and loads class implementing {@link PropertyPattern<T>}
 *
 * @param <T> Type of object to match property on
 */
class PropertyPatternGenerator<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class),
            Type.getType(Property.class), Type.getType(Object.class));
    private static final String MATCHES_DESC = Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class));
    private static final String PACKAGE_NAME = PropertyPatternGenerator.class.getPackage().getName() + ".generated";
    private static final Pattern DOTS = Pattern.compile("\\.");
    private static final String PACKAGE_DESC = DOTS.matcher(PACKAGE_NAME).replaceAll("/");

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
        writer.visit(V1_8, ACC_PUBLIC, generateClassName(), null, type.getInternalName(), null);
        createConstructor(writer);
        createMatches(writer);

        writer.visitEnd();
        return Try.of(() -> {
            CompileDebugger.dumpClass("/tmp/" + generateBinaryClassName() + ".class", writer.toByteArray());
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(), writer.toByteArray());
            final Class propType = property.getType().isPrimitive()
                    ? ClassUtils.primitiveToWrapper(property.getType())
                    : property.getType();
            return condition.getMethod() == ConditionMethod.MATCH
                    ? instantiateMatchPattern(patternClass)
                    : instantiatePattern(patternClass, propType);
        }).getOrElseThrow((ex) -> new CompilerException(ex, "Failed to generate accessor for '%s'", property));
    }

    private PropertyPattern<T> instantiatePattern(final Class<?> patternClass, final Class propType) throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        final Constructor<?> constructor = patternClass.getConstructor(Property.class, propType);
        return (PropertyPattern<T>) constructor.newInstance(property, condition.getValue());
    }

    private PropertyPattern<T> instantiateMatchPattern(final Class<?> patternClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        final Constructor<?> constructor = patternClass.getConstructor(Property.class, Condition.class);
        return (PropertyPattern<T>) constructor.newInstance(property, (Condition) condition.getValue());
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv {@link ClassVisitor} for which constructor will be generated
     */
    private void createConstructor(final ClassVisitor cv) {
        final Class wrapperType =
                condition.getMethod() == ConditionMethod.MATCH
                        ? Condition.class
                        : ClassUtils.primitiveToWrapper(property.getType());
        final String initDescriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(Property.class), Type.getType(wrapperType));
        final MethodVisitor init = cv.visitMethod(ACC_PUBLIC, "<init>", initDescriptor, null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitVarInsn(ALOAD, 1);
        init.visitVarInsn(ALOAD, 2);
        init.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(compGen.getParentPropertyPatternClass()),
                "<init>",
                BASE_INIT_DESCRIPTOR,
                false);
        init.visitInsn(RETURN);
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

        final MethodVisitor match = cv.visitMethod(ACC_PUBLIC, "matches", MATCHES_DESC, "(TT;)Z", null);
        final Label propNotNull = new Label();
        final Label matchingNotNull = new Label();
        final Label matchingNotNull2 = new Label();
        final Label start = new Label();
        final Label end = new Label();

        final Label localMatchingStart = new Label();
        final Label localMatchingEnd = new Label();
        final Label localPropStart = new Label();
        final Label localPropEnd = new Label();

        final int localProperty = compGen.getPropertyLocalIndex();
        final int localMatching = compGen.getMatchingLocalIndex();

        match.visitCode();

        // get matching value and store it to local var
        match.visitLabel(start);
        match.visitVarInsn(ALOAD, 0); // this
        match.visitMethodInsn(INVOKEVIRTUAL,
                Type.getInternalName(compGen.getParentPropertyPatternClass())
                , "getMatchingValue", compGen.getMatchingValueDescriptor(), false);
        match.visitLabel(localMatchingStart);
        match.visitVarInsn(ASTORE, localMatching);

        // get property from instance
        match.visitVarInsn(ALOAD, 1); // instance parameter
        compGen.generatePropertyGet(match, property);
        match.visitLabel(localPropStart);
        match.visitVarInsn(compGen.store(), localProperty);
        // if property type is reference then perform reference checks
        if (compGen.isReference()) {
            // if (property != null) goto comparision
            match.visitVarInsn(compGen.load(), localProperty);
            match.visitJumpInsn(IFNONNULL, propNotNull);

            // property == null, now if matching value == null return true
            match.visitVarInsn(ALOAD, localMatching);
            match.visitJumpInsn(IFNONNULL, matchingNotNull);

            match.visitInsn(ICONST_1);
            match.visitInsn(IRETURN);
            // matching is not null, return false
            match.visitLabel(matchingNotNull);
            match.visitInsn(ICONST_0);
            match.visitInsn(IRETURN);

            match.visitLabel(propNotNull);
        }
        // at this point if matching value is null, return false
        match.visitVarInsn(ALOAD, localMatching);
        match.visitJumpInsn(IFNONNULL, matchingNotNull2);
        // if it is null, return false
        match.visitInsn(ICONST_0);
        match.visitInsn(IRETURN);

        match.visitLabel(matchingNotNull2);
        match.visitVarInsn(compGen.load(), localProperty);

        match.visitVarInsn(ALOAD, localMatching);
        compGen.convertMatchingType(match);

        compGen.generateCompareCode(match);

        match.visitLabel(localMatchingEnd);
        match.visitLabel(localPropEnd);

        match.visitInsn(IRETURN);
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
