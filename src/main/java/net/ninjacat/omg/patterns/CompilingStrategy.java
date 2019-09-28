package net.ninjacat.omg.patterns;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.reflect.ReflectPatternCompiler;

import java.util.function.Function;

public enum CompilingStrategy {
    SAFE(ReflectPatternCompiler::forClass),
    FAST(AsmPatternCompiler::forClass);

    private final Function<Class, PropertyPatternCompiler> provider;

    CompilingStrategy(final Function<Class, PropertyPatternCompiler> provider) {
        this.provider = provider;
    }

    public <T> PropertyPatternCompiler<T> getCompiler(final Class<T> cls) {
        return provider.apply(cls);
    }
}
