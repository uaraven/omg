package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.patterns.PropertyPatternCompiler;

public final class AsmPatternCompiler<T> implements PropertyPatternCompiler<T> {
    private final Class<T> cls;

    public static <T> AsmPatternCompiler<T> forClass(final Class<T> cls) {
        return new AsmPatternCompiler<>(cls);
    }

    private AsmPatternCompiler(final Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public <P> PropertyPattern<T> build(final PropertyCondition<P> condition) {
        return buildPattern(condition);
    }

    private <P> PropertyPattern<T> buildPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        final PropertyPatternGenerator<T> compiler = new PropertyPatternGenerator<>(property, condition);
        return compiler.compilePattern();
    }

    private Property<T> createProperty(final String field) {
        return Property.fromPropertyName(field, cls);
    }

}
