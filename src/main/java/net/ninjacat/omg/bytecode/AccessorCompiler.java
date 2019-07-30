package net.ninjacat.omg.bytecode;

import io.vavr.control.Try;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;

public class AccessorCompiler<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class),
            Type.getType(Property.class), Type.getType(Object.class));
    private static final String GET_MATCHING_DESC = Type.getDescriptor(Object.class);
    private static final String PARENT_INTERNAL_NAME = Type.getInternalName(BasePropertyPattern.class);
    private static final String MATCHES_DESC = Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class));

    private final Property<T> property;
    private PropertyCondition condition;
    private final PatternCompilerStrategy compGen;

    public AccessorCompiler(final Property<T> property, final PropertyCondition condition) {
        this.property = property;
        this.condition = condition;
        final GeneratorKey key = GeneratorKey.of(property.getType(), condition.getMethod());
        this.compGen = TypedPropertyCompilerProvider.COMPILERS.get(key);
    }

    @SuppressWarnings("unchecked")
    public PropertyPattern<T> compilePattern() {
        final ClassReader classReader =
                Try.of(() -> new ClassReader(BasePropertyPattern.class.getName()))
                        .getOrElseThrow(ex -> new CompilerException(ex, "Failed to read base class for compiling"));
        final ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);

        final ClassVisitor cv = new AccessorVisitor(Opcodes.ASM6, writer);
        classReader.accept(cv, 0);

        writer.visitEnd();
        return Try.of(() -> {
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(), writer.toByteArray());
            final Constructor<?> constructor = patternClass.getConstructor(Property.class, Object.class);
            return (PropertyPattern<T>) constructor.newInstance(property, condition.getValue());
        }).getOrElseThrow((ex) -> new CompilerException(ex, "Failed to generate accessor for '%s'", property));
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv           {@link ClassVisitor} for which constructor will be generated
     * @param propertyType Type of the property to access
     * @return {@link MethodVisitor} for the constructor
     */
    public static MethodVisitor createConstructor(final ClassVisitor cv, final Class propertyType) {
        final String initDescriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(Property.class), Type.getType(propertyType));
        final MethodVisitor init = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", initDescriptor, null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitVarInsn(Opcodes.ALOAD, 1);
        init.visitVarInsn(Opcodes.ALOAD, 2);
        init.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                PARENT_INTERNAL_NAME,
                "<init>",
                BASE_INIT_DESCRIPTOR,
                false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);
        init.visitEnd();
        return init;
    }

    /**
     * Generates .matches() implementation.
     * <p>
     * Implementation reads property from passed object instance, performs null checks and then calls provided strategy
     * to generate casting and comparison code
     *
     * @param cv
     * @param property
     * @return
     */
    public MethodVisitor createMatches(
            final ClassVisitor cv,
            final Property property) {

        final MethodVisitor match = cv.visitMethod(Opcodes.ACC_PUBLIC, "matches", MATCHES_DESC, "(TT;)Z", null);
        final Label propNotNull = new Label();
        final Label matchingNotNull = new Label();
        final Label matchingNotNull2 = new Label();

        final int localProperty = 2;
        final int localMatching = 3;

        match.visitCode();

        // get matching value and store it to local var
        match.visitVarInsn(Opcodes.ALOAD, 0);
        match.visitMethodInsn(Opcodes.INVOKEVIRTUAL, PARENT_INTERNAL_NAME, GET_MATCHING_DESC, null, false);
        match.visitVarInsn(Opcodes.ASTORE, localMatching);

        // get property from instance
        match.visitVarInsn(Opcodes.ALOAD, 1);
        compGen.generatePropertyGet(match, property);

        match.visitVarInsn(compGen.store(), localProperty);
        // if property type is reference perform reference checks
        if (compGen.isReference()) {
            // if (property != null) goto comparision
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
        match.visitVarInsn(Opcodes.ALOAD, localMatching);
        compGen.convertMatchingType(match);
        match.visitVarInsn(compGen.load(), localProperty);

        compGen.generateCompareCode(match);
        match.visitInsn(Opcodes.IRETURN);

        match.visitMaxs(0, 0);
        match.visitEnd();
        return match;
    }

    private String generateClassName() {
        return "net/ninjacat/omg/bytecode/generated/PropertyAccessor$" + property.getOwner().getSimpleName() + '$' +
                property.getPropertyName();
    }

    private String generateBinaryClassName() {
        return "net.ninjacat.omg.bytecode.generated.PropertyAccessor$" + property.getOwner().getSimpleName() + '$' +
                property.getPropertyName();
    }

    private class AccessorVisitor extends ClassVisitor {

        AccessorVisitor(final int api, final ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            final Type type = Type.getType(BasePropertyPattern.class);
            super.visit(version, Opcodes.ACC_PUBLIC, generateClassName(), null, type.getInternalName(), interfaces);
        }

        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
            switch (name) {
                case "<init>":
                    return createConstructor(cv, Integer.class);
                case "matches":
                    final MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, name, MATCHES_DESC, signature, exceptions);
                    return createMatches(cv, property);
                default:
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }

    }
}
