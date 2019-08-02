package net.ninjacat.omg;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.patterns.PropertyPatternCompiler;

public final class PatternCompiler {

    private PatternCompiler() {
    }

    public static <T> PropertyPatternCompiler<T> forClass(final Class<T> cls) {
        return AsmPatternCompiler.forClass(cls);
    }

    public static <T> PropertyPatternCompiler<T> forClass(final Class<T> cls, final CompilerSelectionStrategy strategy) {
        return strategy.getCompiler(cls);
    }
}
