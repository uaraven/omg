package net.ninjacat.omg;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.reflection.ReflectPatternCompiler;
import net.ninjacat.omg.patterns.PropertyPatternCompiler;

import java.util.function.Function;

public enum CompilerSelectionStrategy {
    SAFE(ReflectPatternCompiler::forClass),
    FAST(AsmPatternCompiler::forClass);

    private final Function<Class, PropertyPatternCompiler> provider;

    CompilerSelectionStrategy(final Function<Class, PropertyPatternCompiler> provider) {
        this.provider = provider;
    }

    public <T> PropertyPatternCompiler<T> getCompiler(final Class<T> cls) {
        return provider.apply(cls);
    }
}
