package net.ninjacat.objmatcher.reflect;

import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.conditions.*;
import net.ninjacat.objmatcher.errors.MatcherException;
import net.ninjacat.objmatcher.patterns.PropertyPattern;
import net.ninjacat.objmatcher.patterns.PropertyPatternCompiler;

import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;
import static net.ninjacat.objmatcher.reflect.TypeUtils.convertToBasicType;

@SuppressWarnings("FeatureEnvy")
@Immutable
public final class ReflectPatternCompiler<T> implements PropertyPatternCompiler<T> {
    private final Class<T> cls;

    public static <T> ReflectPatternCompiler<T> forClass(final Class<T> cls) {
        return new ReflectPatternCompiler<>(cls);
    }

    private ReflectPatternCompiler(final Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public <P> PropertyPattern<T> build(final PropertyCondition<P> condition) {
        return Match((PropertyCondition) condition).of(
                Case($(instanceOf(EqCondition.class)), this::buildEqPattern),
                Case($(instanceOf(NeqCondition.class)), this::buildNeqPattern),
                Case($(instanceOf(GtCondition.class)), this::buildGtPattern),
                Case($(instanceOf(LtCondition.class)), this::buildLtPattern),
                Case($(instanceOf(RegexCondition.class)), this::buildRegexPattern)
        );
    }

    private <P> PropertyPattern<T> buildEqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongEqPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), doubleProp -> new DoubleEqPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(is(String.class)), strProp -> new StringEqPattern<>(property, toStringOrNull(condition.getValue())))
        );
    }

    private <P> PropertyPattern<T> buildNeqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongNeqPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), doubleProp -> new DoubleNeqPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(is(String.class)), strProp -> new StringNeqPattern<>(property, toStringOrNull(condition.getValue())))
        );
    }

    private <P> PropertyPattern<T> buildGtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongGtPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), doubleProp -> new DoubleGtPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(), () -> {
                    throw new MatcherException("Greater-than expressions are only supported for numeric properties. Got property: %s", property);
                })
        );
    }

    private <P> PropertyPattern<T> buildLtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), longProp -> new LongLtPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), doubleProp -> new DoubleLtPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(), () -> {
                    throw new MatcherException("Less-than expressions are only supported for numeric properties. Got property: %s", property);
                })
        );
    }

    private <P> PropertyPattern<T> buildRegexPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(String.class)), strProp -> new StringRegexPattern<>(property, toStringOrNull(condition.getValue()))),
                Case($(), () -> {
                    throw new MatcherException("Regex expressions are only supported for 'String' properties. Got property: %s", property);
                })
        );
    }

    private PropertyPattern<T> buildObjectPattern(final ObjectCondition condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(this::isBasicType), () -> {
                    throw new MatcherException("Not a nested object. Property: %s ", property);
                }),
                Case($(), () -> new ObjectPattern<>(property, ReflectPatternCompiler.forClass(property.getType()).build(condition)))
        );
    }


    private Property<T> createProperty(final String field) {
        return Property.fromPropertyName(field, cls);
    }

    private String toStringOrNull(final Object value) {
        return Optional.ofNullable(value).map(Object::toString).orElse(null);
    }

    private boolean isBasicType(final Class<T> cls) {
        return cls.equals(Long.class) || cls.equals(Double.class) || cls.equals(String.class);
    }
}