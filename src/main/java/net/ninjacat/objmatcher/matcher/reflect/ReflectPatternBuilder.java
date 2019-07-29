package net.ninjacat.objmatcher.matcher.reflect;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.ninjacat.objmatcher.matcher.conditions.*;
import net.ninjacat.objmatcher.matcher.patterns.PropertyPattern;
import net.ninjacat.objmatcher.matcher.patterns.PropertyPatternBuilder;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;
import static net.ninjacat.objmatcher.matcher.reflect.TypeUtils.convertToBasicType;

@Immutable
public final class ReflectPatternBuilder<T> implements PropertyPatternBuilder<T> {
    private final Class<T> cls;

    public static <T> ReflectPatternBuilder<T> forClass(final Class<T> cls) {
        return new ReflectPatternBuilder<>(cls);
    }

    private ReflectPatternBuilder(final Class<T> cls) {
        this.cls = cls;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> PropertyPattern<T> build(final PropertyCondition<P> condition) {
        return Match((PropertyCondition) condition).of(
                Case($(instanceOf(EqCondition.class)), eq -> buildEqPattern(eq)),
                Case($(instanceOf(NeqCondition.class)), eq -> buildNeqPattern(eq)),
                Case($(instanceOf(GtCondition.class)), eq -> buildGtPattern(eq)),
                Case($(instanceOf(LtCondition.class)), eq -> buildLtPattern(eq))
        );
    }

    private <P> PropertyPattern<T> buildEqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getField());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongEqPattern<>(property, (Long) convertToBasicType(condition.getValue())))
        );
    }

    private <P> PropertyPattern<T> buildNeqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getField());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongNeqPattern<>(property, (Long) convertToBasicType(condition.getValue())))
        );
    }

    private <P> PropertyPattern<T> buildGtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getField());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongGtPattern<>(property, (Long) convertToBasicType(condition.getValue())))
        );
    }

    private <P> PropertyPattern<T> buildLtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getField());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongLtPattern<>(property, (Long) convertToBasicType(condition.getValue())))
        );
    }

    private Property<T> createProperty(final String field) {
        return Property.fromPropertyName(field, cls);
    }
}
