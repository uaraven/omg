/*
 * omg: ReflectPatternCompiler.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.reflect;

import io.vavr.control.Try;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.InCondition;
import net.ninjacat.omg.conditions.ObjectCondition;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import net.ninjacat.omg.errors.OmgException;
import net.ninjacat.omg.errors.PatternException;
import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.patterns.Patterns;
import net.ninjacat.omg.patterns.PropertyPattern;
import net.ninjacat.omg.patterns.PropertyPatternCompiler;

import java.util.Collection;
import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static net.ninjacat.omg.utils.TypeUtils.convertToBasicType;

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
        //noinspection CastToConcreteClass
        return Match(condition.getMethod()).of(
                Case($(is(ConditionMethod.EQ)), m -> buildEqPattern(condition)),
                Case($(is(ConditionMethod.NEQ)), m -> buildNeqPattern(condition)),
                Case($(is(ConditionMethod.GT)), m -> buildGtPattern(condition)),
                Case($(is(ConditionMethod.LT)), m -> buildLtPattern(condition)),
                Case($(is(ConditionMethod.IN)), m -> buildInPattern(condition)),
                Case($(is(ConditionMethod.REGEX)), m -> buildRegexPattern(condition)),
                Case($(allOf(is(ConditionMethod.MATCH), isValidCondition(condition))), m -> buildObjectPattern((ObjectCondition) condition)),
                Case($(), () -> {
                    throw new CompilerException("Cannot build pattern for '%s'", condition);
                })
        );
    }

    @SuppressWarnings("unchecked")
    private <P> BaseInPattern<T, ?> buildInPattern(final PropertyCondition<P> propCondition) {
        @SuppressWarnings("CastToConcreteClass") final InCondition<P> condition = (InCondition<P>) propCondition;
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), l -> new LongInPattern<>(property, (Collection<Long>) condition.getValue())),
                Case($(is(Double.class)), d -> new DoubleInPattern<>(property, (Collection<Double>) condition.getValue())),
                Case($(is(String.class)), s -> new StringInPattern<>(property, (Collection<String>) condition.getValue())),
                Case($(ReflectPatternCompiler::isEnum), e -> new EnumInPattern<>(property, (Collection<Enum>) condition.getValue())),
                Case($(), () -> {
                    throw new CompilerException("Object fields are not supported for IN condition '%s", propCondition);
                })
        );
    }

    private static <P> Predicate<ConditionMethod> isValidCondition(final PropertyCondition<P> condition) {
        return c -> condition instanceof ObjectCondition;
    }

    private <P> PropertyPattern<T> buildEqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Try.of(() -> getPattern(condition, property)).getOrElseThrow(
                err -> Match(err).of(
                        Case($(instanceOf(ClassCastException.class)),
                                (ex) -> new TypeConversionException(ex, condition.getValue(), property.getType())),
                        Case($(instanceOf(OmgException.class)), ex -> ex),
                        Case($(), (ex) -> new PatternException("Error while building pattern for condition '%s'", condition))
                )
        );
    }

    private <P> PropertyPattern<T> getPattern(final PropertyCondition<P> condition, final Property<T> property) {
        final Class type = property.getWidenedType();
        //noinspection IfStatementWithTooManyBranches
        if (type.equals(Long.class)) {
            return new LongEqPattern<>(property, (Long) convertToBasicType(condition.getValue()));
        } else if (type.equals(Double.class)) {
            return new DoubleEqPattern<>(property, (Double) convertToBasicType(condition.getValue()));
        } else if (type.equals(String.class)) {
            return new StringEqPattern<>(property, forceToString(condition.getValue()));
        } else if (type.equals(Boolean.class)) {
            return new BooleanEqPattern<>(property, (Boolean) condition.getValue());
        } else if (isEnum(type)) {
            return new EnumEqPattern<>(property, (Enum) condition.getValue());
        } else {
            return new ObjectEqPattern<>(property, condition.getValue());
        }
    }

    private <P> PropertyPattern<T> buildNeqPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        final Class type = property.getWidenedType();

        //noinspection IfStatementWithTooManyBranches
        if (type.equals(Long.class)) {
            return new LongNeqPattern<>(property, (Long) convertToBasicType(condition.getValue()));
        } else if (type.equals(Double.class)) {
            return new DoubleNeqPattern<>(property, (Double) convertToBasicType(condition.getValue()));
        } else if (type.equals(String.class)) {
            return new StringNeqPattern<>(property, forceToString(condition.getValue()));
        } else if (type.equals(Boolean.class)) {
            return new BooleanNeqPattern<>(property, (Boolean) condition.getValue());
        } else if (isEnum(type)) {
            return new EnumNeqPattern<>(property, (Enum) condition.getValue());
        } else {
            return new ObjectNeqPattern<>(property, condition.getValue());
        }
    }

    private <P> PropertyPattern<T> buildGtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), $_ -> new LongGtPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), $_ -> new DoubleGtPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(), () -> {
                    throw new CompilerException("Greater-than expressions are only supported for numeric properties. Got property: %s", property);
                })
        );
    }

    private <P> PropertyPattern<T> buildLtPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(Long.class)), $_ -> new LongLtPattern<>(property, (Long) convertToBasicType(condition.getValue()))),
                Case($(is(Double.class)), $_ -> new DoubleLtPattern<>(property, (Double) convertToBasicType(condition.getValue()))),
                Case($(), () -> {
                    throw new CompilerException("Less-than expressions are only supported for numeric properties. Got property: %s", property);
                })
        );
    }

    private <P> PropertyPattern<T> buildRegexPattern(final PropertyCondition<P> condition) {
        final Property<T> property = createProperty(condition.getProperty());
        return Match(property.getWidenedType()).of(
                Case($(is(String.class)), $_ -> new StringRegexPattern<>(property, forceToString(condition.getValue()))),
                Case($(), () -> {
                    throw new CompilerException("Regex expressions are only supported for 'String' properties. Got property: %s", property);
                })
        );
    }

    @SuppressWarnings("unchecked")
    private PropertyPattern<T> buildObjectPattern(final ObjectCondition condition) {
        final Property<T> property = createProperty(condition.getProperty());
        if (isBasicType(property.getWidenedType())) {
            throw new CompilerException("Not a nested object. Property: %s ", property);
        } else {
            return new ObjectPattern<>(property,
                    Patterns.compile(condition.getValue(), ReflectPatternCompiler.forClass(property.getType())));

        }
    }

    private Property<T> createProperty(final String field) {
        return Property.fromPropertyName(field, cls);
    }

    private static String forceToString(final Object value) {
        return Try.of(() -> (String) value)
                .getOrElseThrow((err) -> new TypeConversionException(err, value, String.class));
    }

    private static boolean isEnum(final Class cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    private static boolean isObject(final Class cls) {
        return true;
    }

    private static boolean isBasicType(final Class cls) {
        return cls.equals(Long.class) || cls.equals(Double.class) || cls.equals(String.class);
    }
}
