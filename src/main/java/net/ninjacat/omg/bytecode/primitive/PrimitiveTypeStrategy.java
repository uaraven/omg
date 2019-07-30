package net.ninjacat.omg.bytecode.primitive;

import io.vavr.control.Try;
import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.bytecode.Property;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Compilation strategy for integer reference types (Long, Integer, Byte, Short, Character)
 */
public abstract class PrimitiveTypeStrategy implements PatternCompilerStrategy {


    @Override
    public boolean isReference() {
        return false;
    }

}
