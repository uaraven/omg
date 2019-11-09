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
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.LogicalCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;
import java.util.regex.Pattern;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static org.objectweb.asm.Opcodes.*;

/**
 * Generates and loads class implementing {@link PropertyPattern<T>}
 *
 * @param <T> Type of object to match property on
 */
class MatcherGenerator<T> {

    private static final String BASE_INIT_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(void.class),
            Type.getType(Property.class), Type.getType(Object.class));
    private static final String MATCHES_DESC = Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(Object.class));
    private static final String PACKAGE_NAME = MatcherGenerator.class.getPackage().getName() + ".generated";
    private static final Pattern DOTS = Pattern.compile("\\.");
    private static final String PACKAGE_DESC = DOTS.matcher(PACKAGE_NAME).replaceAll("/");

    private final Class<T> targetClass;
    private final Condition condition;

    MatcherGenerator(final Class<T> targetClass, final Condition condition) {
        this.targetClass = targetClass;
        this.condition = condition;
    }

    PropertyPattern<T> compilePattern() {
        final ClassReader classReader =
                Try.of(() -> new ClassReader(BasePropertyPattern.class.getName()))
                        .getOrElseThrow(ex -> new CompilerException(ex, "Failed to read base class for compiling"));
        final ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        final Type type = Type.getType(BasePropertyPattern.class);
        final String className = generateClassName();
        writer.visit(V1_8, ACC_PUBLIC, className, null, type.getInternalName(), null);
        createConstructor(writer);

        createMatches(writer);

        writer.visitEnd();
        return Try.of(() -> {
            final Class<?> patternClass = new CompiledClassLoader().defineClass(generateBinaryClassName(className), writer.toByteArray());
            CompileDebugger.dumpClass("/tmp/dump.class", writer.toByteArray());
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
                Type.getInternalName(BasePropertyPattern.class),
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
        final Label start = new Label();
        final Label end = new Label();

        match.visitCode();

        generateMatcherCode(match, targetClass, condition);

        match.visitInsn(IRETURN);
        match.visitLabel(end);

        match.visitMaxs(0, 0);
        match.visitLocalVariable("this", "L" + generateClassName() + ";", null, start, end, 0);
        match.visitLocalVariable("instance", Type.getDescriptor(Object.class), null, start, end, 1);

        match.visitEnd();
    }

    private void generateMatcherCode(final MethodVisitor match, final Class<?> targetClass, final Condition condition) {
        Match(condition).of(
                Case($(instanceOf(LogicalCondition.class)), lc -> run(() -> generateLogicalCondition(match, lc))),
                Case($(instanceOf(PropertyCondition.class)), pc -> run(() -> generatePropertyCondition(match, pc))),
                Case($(), other -> {
                    throw new CompilerException("Unsupported condition type: {}", condition);
                })
        );
    }

    private void generateLogicalCondition(MethodVisitor match, LogicalCondition oc) {

    }

    private void generatePropertyCondition(MethodVisitor match, PropertyCondition oc) {

    }


    private String generateClassName() {
        return PACKAGE_DESC + "/" + Long.toHexString(new Random().nextLong()) + '$' + "Matcher";
    }

    private String generateBinaryClassName(final String className) {
        return className.replaceAll("/", ".");
    }

    private Property<T> createProperty(final String field) {
        return Property.fromPropertyName(field, targetClass);
    }
}
