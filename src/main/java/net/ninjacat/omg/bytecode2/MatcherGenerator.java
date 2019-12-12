/*
 * omg: PropertyPatternGenerator.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.bytecode2;

import io.vavr.control.Try;
import net.ninjacat.omg.bytecode.CompileDebugger;
import net.ninjacat.omg.bytecode.CompiledClassLoader;
import net.ninjacat.omg.bytecode2.primitive.IntGeneratorProvider;
import net.ninjacat.omg.conditions.*;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;
import java.util.stream.IntStream;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static org.objectweb.asm.Opcodes.*;

/**
 * Generates and loads class implementing {@link PropertyPattern<T>}
 *
 * @param <T> Type of object to match property on
 */
class MatcherGenerator<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class));
    private static final String PACKAGE_NAME = MatcherGenerator.class.getPackage().getName() + ".generated";
    private static final java.util.regex.Pattern DOTS = java.util.regex.Pattern.compile("\\.");
    private static final String PACKAGE_DESC = DOTS.matcher(PACKAGE_NAME).replaceAll("/");

    private final Class<T> targetClass;
    private final Condition condition;

    MatcherGenerator(final Class<T> targetClass, final Condition condition) {
        this.targetClass = targetClass;
        this.condition = condition;
    }

    Pattern<T> compilePattern() {
        return compilePattern(CompilationOptions.getDefaults());
    }

    Pattern<T> compilePattern(final CompilationOptions options) {
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        final String className = generateClassName();
        writer.visit(V1_8, ACC_PUBLIC, className, null, Codes.OBJECT_NAME, new String[]{Type.getInternalName(PropertyPattern.class)});

        final CodeGenerationContext<T> context = ImmutableCodeGenerationContext.<T>builder()
                .classVisitor(writer)
                .matcherClassName(className)
                .targetClass(targetClass)
                .build();

        createConstructor(writer);

        createMatches(writer, context);

        writer.visitEnd();
        return Try.of(() -> {
            if (options.dumpToFile() != null) {
                CompileDebugger.dumpClass(options.dumpToFile(), writer.toByteArray());
            }
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(className), writer.toByteArray());
            return instantiatePattern(patternClass);
        }).getOrElseThrow(ex -> wrapException(ex, context));
    }

    private RuntimeException wrapException(final Throwable ex, final CodeGenerationContext<T> context) {
        return Match(ex).of(
                Case($(instanceOf(OmgException.class)), err -> err),
                Case($(), err -> new CompilerException(err, "Failed to generate matcher for '%s'", context.targetClass()))
        );
    }

    @SuppressWarnings("unchecked")
    private Pattern<T> instantiatePattern(final Class<?> patternClass) throws Throwable {
        final MethodType constructorType = MethodType.methodType(void.class);
        final MethodHandle constructor = MethodHandles.lookup().findConstructor(patternClass, constructorType);
        return (Pattern<T>) constructor.invoke();
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv {@link ClassVisitor} for which constructor will be generated
     */
    private void createConstructor(final ClassVisitor cv) {
        final String initDescriptor = Type.getMethodDescriptor(Type.getType(void.class));
        final MethodVisitor init = cv.visitMethod(ACC_PUBLIC, "<init>", initDescriptor, null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitMethodInsn(
                INVOKESPECIAL,
                Codes.OBJECT_NAME,
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
    private void createMatches(final ClassVisitor cv, final CodeGenerationContext<T> context) {
        final String matchesDesc = Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(context.targetClass()));

        generateMatchesBootstrap(cv, context, matchesDesc);
        generateStaticMatchMethod(cv, context, matchesDesc);

        context.props().postGenerators().forEach(Runnable::run);
    }

    private void generateStaticMatchMethod(final ClassVisitor cv, final CodeGenerationContext<T> context, final String matchesDesc) {
        final MethodVisitor match = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, "_matches", matchesDesc, null, null);
        final Label start = new Label();
        final Label end = new Label();

        match.visitCode();
        match.visitLabel(start);

        generateMatcherCode(match, condition, context);

        match.visitInsn(IRETURN);
        match.visitLabel(end);

        match.visitMaxs(0, 0);
        match.visitLocalVariable("instance", Type.getDescriptor(context.targetClass()), null, start, end, 0);

        match.visitEnd();
    }

    /**
     * Generates matches method which overrides {@link Pattern#matches(Object)}.
     * <p>
     * This method will cast parameter to correct type and call internal {@code _matches} method to perform actual matching
     *
     * @param cv          Class visitor used to generate methods
     * @param context     Generation context containing various parameters
     * @param matchesDesc Descriptor for _matches method
     */
    private void generateMatchesBootstrap(final ClassVisitor cv, final CodeGenerationContext<T> context, final String matchesDesc) {
        final String bootstrapDesc = Type.getMethodDescriptor(Type.getType(boolean.class), Codes.OBJECT_TYPE);
        final MethodVisitor match = cv.visitMethod(ACC_PUBLIC, "matches", bootstrapDesc, null, null);
        match.visitAnnotation(Type.getDescriptor(Override.class), true);
        final Label start = new Label();
        final Label end = new Label();
        match.visitLabel(start);
        match.visitVarInsn(ALOAD, 1);
        match.visitTypeInsn(CHECKCAST, Type.getInternalName(context.targetClass()));
        match.visitMethodInsn(INVOKESTATIC, context.matcherClassName(), "_matches", matchesDesc, false);
        match.visitLabel(end);
        match.visitInsn(IRETURN);
        match.visitMaxs(0, 0);
        match.visitLocalVariable("this", "L" + generateClassName() + ";", null, start, end, 0);
        match.visitLocalVariable("instance", Codes.OBJECT_DESC, null, start, end, 1);
    }

    private void generateMatcherCode(final MethodVisitor match, final Condition condition, final CodeGenerationContext<T> context) {
        Match(condition).of(
                Case($(instanceOf(LogicalCondition.class)), lc -> run(() -> generateLogicalCondition(match, lc, context))),
                Case($(instanceOf(PropertyCondition.class)), pc -> run(() -> generatePropertyCondition(match, pc, context))),
                Case($(), other -> {
                    throw new CompilerException("Unsupported condition type: %s", condition);
                })
        );
    }

    private void generateLogicalCondition(final MethodVisitor match, final LogicalCondition lc, final CodeGenerationContext<T> context) {
        if (lc instanceof NotCondition) {
            if (lc.getChildren().size() != 1) {
                throw new CompilerException("NOT condition only can contain one operand");
            }
            generateMatcherCode(match, lc.getChildren().get(0), context);
            Codes.logicalNot(match);
        } else {
            // todo: rewrite with short-circuiting
            lc.getChildren().forEach(cond -> generateMatcherCode(match, cond, context));
            final int opcode = operatorFromCondition(lc);
            IntStream.range(0, lc.getChildren().size() - 1).forEach($ -> match.visitInsn(opcode));
        }
    }

    private int operatorFromCondition(final LogicalCondition lc) {
        return Match(lc).of(
                Case($(instanceOf(AndCondition.class)), x -> IAND),
                Case($(instanceOf(OrCondition.class)), x -> IOR),
                Case($(), x -> {
                    throw new CompilerException("Either AND or OR condition expected, but '%s' found", lc.getMethod());
                })
        );
    }

    private void generatePropertyCondition(final MethodVisitor match, final PropertyCondition<T> pc, final CodeGenerationContext<T> context) {
        generateCode(context, match, pc);
    }


    private String generateClassName() {
        return PACKAGE_DESC + "/" + "Gen" + Long.toHexString(new Random().nextLong()) + '$' + "Matcher";
    }

    private String generateBinaryClassName(final String className) {
        return className.replaceAll("/", ".");
    }


    public <P, V> void generateCode(
            final CodeGenerationContext<T> context,
            final MethodVisitor method,
            final PropertyCondition<V> condition) {
        final Property<T, P> property = createProperty(condition.getProperty(), context.targetClass());

        final TypedCodeGenerator<T, P, V> codeGen = getGeneratorFor(property.getType(), condition, context);

        codeGen.prepareStackForCompare(property, condition, method);
        codeGen.compare(condition, method);
    }

    private <P, V> TypedCodeGenerator<T, P, V> getGeneratorFor(final Class<?> type, final PropertyCondition<V> condition, final CodeGenerationContext<T> context) {
        if (int.class.equals(type)) {
            return (TypedCodeGenerator<T, P, V>) IntGeneratorProvider.getGenerator(condition, context);
        } else {
            throw new CompilerException("Type '%s' is not supported", type.getName());
        }
    }

    private <T, P> Property<T, P> createProperty(final String field, final Class<T> targetClass) {
        return Property.fromPropertyName(field, targetClass);
    }

}
