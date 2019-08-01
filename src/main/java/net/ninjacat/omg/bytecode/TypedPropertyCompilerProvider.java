package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.bytecode.reference.*;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

final class TypedPropertyCompilerProvider {

    private TypedPropertyCompilerProvider() {
    }

    static PatternCompilerStrategy getGeneratorFor(final Class cls, final ConditionMethod method) {
        return Match(cls).of(
                Case($(is(Integer.class)), intCls -> IntegerStrategy.forMethod(method)),
                Case($(is(Long.class)), longCls -> LongStrategy.forMethod(method)),
                Case($(is(Short.class)), s -> ShortStrategy.forMethod(method)),
                Case($(is(Byte.class)), s -> ByteStrategy.forMethod(method)),
                Case($(is(Character.class)), s -> CharacterStrategy.forMethod(method)),
                Case($(), () -> {
                    throw new CompilerException("Cannot find compiler for property of class '%s' and maching operation '%s'",
                            cls.getName(),
                            method);
                })
        );
    }

}
