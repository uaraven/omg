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
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.bytecode2.generator.ImmutableCodeGenerationContext;
import net.ninjacat.omg.bytecode2.primitive.*;
import net.ninjacat.omg.bytecode2.reference.*;
import net.ninjacat.omg.bytecode2.types.CompatibilityProvider;
import net.ninjacat.omg.conditions.*;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;
import static org.objectweb.asm.Opcodes.*;

/**
 * Generates and loads class implementing {@link Pattern<T>}
 *
 * @param <T> Type of object to match property on
 */
class MatcherGenerator<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class));
    private static final String PACKAGE_NAME = MatcherGenerator.class.getPackage().getName() + ".generated";
    private static final java.util.regex.Pattern DOTS = java.util.regex.Pattern.compile("\\.");
    private static final String PACKAGE_DESC = DOTS.matcher(PACKAGE_NAME).replaceAll("/");
    private static final CompiledClassLoader LOADER = new CompiledClassLoader();

    private final Class<T> targetClass;
    private final Condition condition;

    MatcherGenerator(final Class<T> targetClass, final Condition condition) {
        this.targetClass = targetClass;
        this.condition = condition;
    }

    PropertyPattern<T> compilePattern() {
        return compilePattern(CompilationOptions.getDefaults());
    }

    PropertyPattern<T> compilePattern(final CompilationOptions options) {
        final Try<Class<PropertyPattern<T>>> tryPatternClass = generatePatternClass(options);
        return tryPatternClass.mapTry(this::instantiatePattern).getOrElseThrow(this::wrapException);
    }

    Class<PropertyPattern<T>> compilePatternClass() {
        return compilePatternClass(CompilationOptions.getDefaults());
    }

    Class<PropertyPattern<T>> compilePatternClass(final CompilationOptions options) {
        final Try<Class<PropertyPattern<T>>> tryPatternClass = generatePatternClass(options);
        return tryPatternClass.getOrElseThrow(this::wrapException);
    }

    @SuppressWarnings("unchecked")
    Try<Class<PropertyPattern<T>>> generatePatternClass(final CompilationOptions options) {
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        final String className = generateClassName();
        writer.visit(
                V1_8,
                ACC_PUBLIC + ACC_FINAL,
                className,
                null,
                Codes.OBJECT_NAME,
                new String[]{Type.getInternalName(PropertyPattern.class)});

        final CodeGenerationContext context = ImmutableCodeGenerationContext.builder()
                .classVisitor(writer)
                .matcherClassName(className)
                .targetClass(targetClass)
                .build();

        createMatches(writer, context);

        createConstructor(writer, context);

        writer.visitEnd();
        return Try.of(() -> {
            if (options.dumpToFile() != null) {
                CompileDebugger.dumpClass(options.dumpToFile(), writer.toByteArray());
            }
            final String generatedClassName = generateBinaryClassName(className);
            return (Class<PropertyPattern<T>>) LOADER.defineClass(generatedClassName, writer.toByteArray());
        });
    }

    private RuntimeException wrapException(final Throwable ex) {
        return Match(ex).of(
                Case($(instanceOf(OmgException.class)), err -> err),
                Case($(), err -> new CompilerException(err, "Failed to generate matcher for '%s'", targetClass))
        );
    }

    @SuppressWarnings("unchecked")
    private PropertyPattern<T> instantiatePattern(final Class<?> patternClass) throws Throwable {
        final MethodType constructorType = MethodType.methodType(void.class);
        final MethodHandle constructor = MethodHandles.lookup().findConstructor(patternClass, constructorType);
        return (PropertyPattern<T>) constructor.invoke();
    }

    /**
     * Generates constructor for property accessor. Generated constructor will call super constructor.
     *
     * @param cv      {@link ClassVisitor} for which constructor will be generated
     * @param context
     */
    private static void createConstructor(final ClassVisitor cv, final CodeGenerationContext context) {
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
        context.props().postConstructors().forEach(generator -> generator.generateConstructorCode(init, context));
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
    private void createMatches(final ClassVisitor cv, final CodeGenerationContext context) {
        final String matchesDesc = Type.getMethodDescriptor(Type.getType(boolean.class), Codes.OBJECT_TYPE);
        generateMatchMethod(cv, context, matchesDesc);
    }

    private void generateMatchMethod(final ClassVisitor cv, final CodeGenerationContext context, final String matchesDesc) {
        final MethodVisitor match = cv.visitMethod(ACC_PUBLIC, "matches", matchesDesc, null, null);
        final Label start = new Label();
        final Label end = new Label();

        match.visitAnnotation(Type.getDescriptor(Override.class), true);
        match.visitCode();
        match.visitLabel(start);

        match.visitVarInsn(ALOAD, 1);
        match.visitTypeInsn(CHECKCAST, Type.getInternalName(context.targetClass()));
        match.visitVarInsn(ASTORE, Codes.MATCHED_LOCAL);

        generateMatcherCode(match, condition, context);

        match.visitInsn(IRETURN);
        match.visitLabel(end);

        match.visitLocalVariable("this", "L" + generateClassName() + ";", null, start, end, 0);
        match.visitLocalVariable("instance", Codes.OBJECT_DESC, null, start, end, 1);
        match.visitLocalVariable("matchTarget", Type.getDescriptor(context.targetClass()), null, start, end, 2);
        match.visitMaxs(0, 0);

        match.visitEnd();
    }

    private void generateMatcherCode(final MethodVisitor match, final Condition condition, final CodeGenerationContext context) {
        Match(condition).of(
                Case($(instanceOf(LogicalCondition.class)), lc -> run(() -> generateLogicalCondition(match, lc, context))),
                Case($(instanceOf(PropertyCondition.class)), pc -> run(() -> generatePropertyCondition(match, pc, context))),
                Case($(), other -> {
                    throw new CompilerException("Unsupported condition type: %s", condition);
                })
        );
    }

    private void generateLogicalCondition(final MethodVisitor match, final LogicalCondition lc, final CodeGenerationContext context) {
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

    private static int operatorFromCondition(final LogicalCondition lc) {
        return Match(lc).of(
                Case($(instanceOf(AndCondition.class)), x -> IAND),
                Case($(instanceOf(OrCondition.class)), x -> IOR),
                Case($(), x -> {
                    throw new CompilerException("Either AND or OR condition expected, but '%s' found", lc.getMethod());
                })
        );
    }

    private void generatePropertyCondition(final MethodVisitor match, final PropertyCondition<T> pc, final CodeGenerationContext context) {
        generateCode(context, match, pc);
    }


    private static String generateClassName() {
        return PACKAGE_DESC + "/" + "Gen" + Long.toHexString(new Random().nextLong()) + '$' + "Matcher";
    }

    private static String generateBinaryClassName(final String className) {
        return className.replaceAll("/", ".");
    }


    @SuppressWarnings("unchecked")
    public <P, V> void generateCode(
            final CodeGenerationContext context,
            final MethodVisitor method,
            final PropertyCondition<V> condition) {
        final Property<T, P> property = createProperty(condition.getProperty(), context.targetClass());

        validateType(property.getType(), condition);

        final TypedCodeGenerator<T, P, V> codeGen = getGeneratorFor(property.getType(), condition, context);

        codeGen.prepareStackForCompare(property, condition, method);
        codeGen.compare(condition, method);
    }

    @SuppressWarnings({"rawtypes"})
    private static <V> void validateType(final Class propertyType, final PropertyCondition<V> condition) {
//        if (!CompatibilityProvider.forClass(propertyType).canBeAssigned(condition)) {
//            throw new TypeConversionException(condition.getValue().getClass(), condition.getValue(), propertyType);
//        }
    }

    @SuppressWarnings("rawtypes")
    private <P, V> TypedCodeGenerator<T, P, V> getGeneratorFor(final Class type, final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return Match(type).of(
                Case($(is(int.class)), () -> getPrimitiveIntGenerator(condition, context)),
                Case($(is(byte.class)), () -> getPrimitiveIntGenerator(condition, context)),
                Case($(is(short.class)), () -> getPrimitiveIntGenerator(condition, context)),
                Case($(is(char.class)), () -> getPrimitiveIntGenerator(condition, context)),
                Case($(is(long.class)), () -> getPrimitiveLongGenerator(condition, context)),
                Case($(is(float.class)), () -> getPrimitiveFloatGenerator(condition, context)),
                Case($(is(double.class)), () -> getPrimitiveDoubleGenerator(condition, context)),
                Case($(is(boolean.class)), () -> getPrimitiveBooleanGenerator(condition, context)),
                Case($(is(String.class)), () -> getStringGenerator(condition, context)),
                Case($(isComparableNumber()), () -> getComparableGenerator(condition, context)),
                Case($(is(Character.class)), () -> getComparableGenerator(condition, context)),
                Case($(is(Boolean.class)), () -> getBoxedBooleanGenerator(condition, context)),
                Case($(isEnum()), () -> getEnumGenerator(condition, context)),
                Case($(), () -> getObjectGenerator(condition, context))
        );
    }

    @SuppressWarnings("rawtypes")
    private static Predicate<Class> isComparableNumber() {
        return type -> Number.class.isAssignableFrom(type) && Comparable.class.isAssignableFrom(type);
    }

    @SuppressWarnings({"rawtypes"})
    private static Predicate<Class> isEnum() {
        return Enum.class::isAssignableFrom;
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getObjectGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) ObjectGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getComparableGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) ComparableGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getBoxedBooleanGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) BoxedBooleanGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getPrimitiveIntGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) IntGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getPrimitiveBooleanGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) BooleanGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getPrimitiveLongGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) LongGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getPrimitiveDoubleGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) DoubleGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getPrimitiveFloatGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) FloatGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getStringGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) StringGeneratorProvider.getGenerator(condition, context);
    }

    @SuppressWarnings("unchecked")
    private <P, V> TypedCodeGenerator<T, P, V> getEnumGenerator(final PropertyCondition<V> condition, final CodeGenerationContext context) {
        return (TypedCodeGenerator<T, P, V>) EnumGeneratorProvider.getGenerator(condition, context);
    }


    private static <T, P> Property<T, P> createProperty(final String field, final Class<T> targetClass) {
        return Property.fromPropertyName(field, targetClass);
    }

}
