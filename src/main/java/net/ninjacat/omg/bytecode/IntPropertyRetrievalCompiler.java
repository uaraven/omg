package net.ninjacat.omg.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.signature.SignatureReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.ASM6;

public class IntPropertyRetrievalCompiler {
    private static final Method INT_VALUE_OF = getValueOf();

    private static Method getValueOf() {
        try {
            return Method.getMethod(Integer.class.getMethod("valueOf", int.class));
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> byte[] compile(final Property<T> property) throws Exception {
        final ClassReader classReader = new ClassReader(BasePropertyPattern.class.getName());
        final ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        final ClassVisitor cv = new ClassVisitor(ASM6, writer) {

            @Override
            public void visitSource(final String source, final String debug) {
                super.visitSource("runtime-generated", debug);
            }

            @Override
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                final Type type = Type.getType(BasePropertyPattern.class);
                final String newSignature = "<T:Ljava/lang/Object;>Lnet/ninjacat/omg/bytecode/BasePropertyPattern<TT;Ljava/lang/Integer;>;";
                super.visit(version, Opcodes.ACC_PUBLIC, "net/ninjacat/omg/bytecode/IntPropRetriever", newSignature, type.getInternalName(), interfaces);
            }

            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
                switch (name) {
                    case "<init>":
                        final MethodVisitor init = super.visitMethod(Opcodes.ACC_PUBLIC, name, "(Lnet/ninjacat/omg/reflect/Property;Ljava/lang/Integer;)V", signature, exceptions);
                        return generateConstructor(init);
                    case "getPropertyValue":
                        final Type integerType = Type.getType(Integer.class);
                        final Method getPropertyValue = new Method("getPropertyValue", integerType, new Type[]{Type.getType(Object.class)});
                        final MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, name, getPropertyValue.getDescriptor(), signature, exceptions);
                        return generateCode(mv, property);
                    default:
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        };

        classReader.accept(cv, 0);
        final byte[] bytes = writer.toByteArray();
        Files.write(Paths.get("/tmp/Test.class"), bytes);
        return bytes;
    }

    private MethodVisitor generateConstructor(final MethodVisitor init) {
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitVarInsn(Opcodes.ALOAD, 1);
        init.visitVarInsn(Opcodes.ALOAD, 2);
        init.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "net/ninjacat/omg/bytecode/BasePropertyPattern",
                "<init>",
                "(Lnet/ninjacat/omg/reflect/Property;Ljava/lang/Object;)V",
                false);
        init.visitInsn(Opcodes.RETURN);
        init.visitEnd();
        return init;
    }

    private <T> MethodVisitor generateCode(final MethodVisitor mv, final Property<T> property) {
        final Type integerType = Type.getType(Integer.class);
        final Type ownerType = Type.getType(property.getOwner());
        mv.visitAnnotation(Override.class.getTypeName(), true);

        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 1);

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ownerType.getInternalName(), property.getMethod().getName(), property.getMethod().getDescriptor(), false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, integerType.getInternalName(), INT_VALUE_OF.getName(), INT_VALUE_OF.getDescriptor(), false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitEnd();
        return mv;
    }
}
