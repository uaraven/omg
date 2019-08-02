package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.bytecode.primitive.IntStrategy;
import net.ninjacat.omg.bytecode.primitive.PrimitiveLongStrategy;
import net.ninjacat.omg.bytecode.reference.*;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

final class CompilerProvider {

    private CompilerProvider() {
    }

    static <T> PropertyPatternGenerator<T> getPatternGenerator(final Property<T> property, final PropertyCondition condition) {
        final PatternCompilerStrategy strategy = getStrategyFor(property.getType(), condition.getMethod());
        return new PropertyPatternGenerator<>(property, condition, strategy);
    }

    private static PatternCompilerStrategy getStrategyFor(final Class cls, final ConditionMethod method) {
        return Match(cls).of(
                Case($(is(int.class)), intCls -> IntStrategy.forMethod(method, int.class)),
                Case($(is(short.class)), intCls -> IntStrategy.forMethod(method, short.class)),
                Case($(is(byte.class)), intCls -> IntStrategy.forMethod(method, byte.class)),
                Case($(is(char.class)), intCls -> IntStrategy.forMethod(method, char.class)),
                Case($(is(long.class)), l -> PrimitiveLongStrategy.forMethod(method)),
                Case($(is(Integer.class)), intCls -> IntegerStrategy.forMethod(method)),
                Case($(is(Long.class)), longCls -> LongStrategy.forMethod(method)),
                Case($(is(Short.class)), s -> ShortStrategy.forMethod(method)),
                Case($(is(Byte.class)), s -> ByteStrategy.forMethod(method)),
                Case($(is(Double.class)), s -> DoubleStrategy.forMethod(method)),
                Case($(is(Float.class)), s -> FloatStrategy.forMethod(method)),
                Case($(is(Character.class)), s -> CharacterStrategy.forMethod(method)),
                Case($(is(String.class)), s -> StringStrategyProvider.forMethod(method)),
                Case($((Predicate<Class>) Enum.class::isAssignableFrom), e -> EnumStrategy.forMethod(method)),
                Case($(), () -> {
                    throw new CompilerException("Cannot find compiler for property of class '%s' and matching operation '%s'",
                            cls.getName(),
                            method);
                })
        );
    }


}
