package net.ninjacat.omg.patterns;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;

public final class PatternCompiler {

    private PatternCompiler() {
    }

    public static <T> PropertyPatternCompiler<T> forClass(final Class<T> cls) {
        return AsmPatternCompiler.forClass(cls);
    }

    public static <T> PropertyPatternCompiler<T> forClass(final Class<T> cls, final CompilingStrategy strategy) {
        return strategy.getCompiler(cls);
    }
}
