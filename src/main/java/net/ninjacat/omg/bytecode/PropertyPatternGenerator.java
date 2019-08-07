package net.ninjacat.omg.bytecode;

import io.vavr.control.Try;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.regex.Pattern;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static net.ninjacat.omg.bytecode.CompareOrdering.PROPERTY_THEN_MATCHING;
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
            CompileDebugger.verifyClass(writer.toByteArray());
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(), writer.toByteArray());
            return instantiatePattern(patternClass);
        }).getOrElseThrow(this::wrapException);
    }

    private RuntimeException wrapException(final Throwable ex) {
        return Match(ex).of(
                Case($(instanceOf(OmgException.class)), err -> err),
                Case($(), err -> new CompilerException(err, "Failed to generate accessor for '%s'", property))
        );
    }

    @SuppressWarnings("unchecked")
    private PropertyPattern<T> instantiatePattern(final Class<?> patternClass) throws Throwable {
        final MethodType constructorType = MethodType.methodType(void.class, Property.class, Object.class);
        final MethodHandle constructor = MethodHandles.lookup().findConstructor(patternClass, constructorType);
        return (PropertyPattern<T>) constructor.invoke(property, condition.getValue());
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv {@link ClassVisitor} for which constructor will be generated
     */
    private void createConstructor(final ClassVisitor cv) {
        final Class wrapperType = Object.class;
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
                Type.getInternalName(compGen.getParentPropertyPatternClass()),
                "getMatchingValue", compGen.getMatchingValueDescriptor(), false);
        match.visitLabel(localMatchingStart);
        match.visitVarInsn(compGen.matchingStore(), localMatching);

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
            match.visitVarInsn(compGen.matchingLoad(), localMatching);
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
        if (compGen.isReference()) {
            match.visitVarInsn(compGen.matchingLoad(), localMatching);
            match.visitJumpInsn(IFNONNULL, matchingNotNull2);
            // if it is null, return false
            match.visitInsn(ICONST_0);
            match.visitInsn(IRETURN);
            match.visitLabel(matchingNotNull2);
        }

        compGen.beforeCompare(match);

        if (compGen.compareOrdering() == PROPERTY_THEN_MATCHING) {
            match.visitVarInsn(compGen.load(), localProperty);
            match.visitVarInsn(compGen.matchingLoad(), localMatching);
        } else {
            match.visitVarInsn(compGen.matchingLoad(), localMatching);
            match.visitVarInsn(compGen.load(), localProperty);
        }

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
