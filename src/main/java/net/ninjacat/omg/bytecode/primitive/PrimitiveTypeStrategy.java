package net.ninjacat.omg.bytecode.primitive;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;

/**
 * Compilation strategy for integer reference types (Long, Integer, Byte, Short, Character)
 */
public abstract class PrimitiveTypeStrategy implements PatternCompilerStrategy {

    @Override
    public boolean isReference() {
        return false;
    }

}
