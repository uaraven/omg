package net.ninjacat.omg.bytecode;

import net.ninjacat.omg.bytecode.reference.IntegerStrategy;
import net.ninjacat.omg.bytecode.reference.LongStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;

import java.util.Map;

final class TypedPropertyCompilerProvider {
    static final Map<GeneratorKey, PatternCompilerStrategy> COMPILERS =
            io.vavr.collection.HashMap.of(
                    GeneratorKey.of(Integer.class, ConditionMethod.EQ), IntegerStrategy.forMethod(ConditionMethod.EQ),
                    GeneratorKey.of(Integer.class, ConditionMethod.NEQ), IntegerStrategy.forMethod(ConditionMethod.NEQ),
                    GeneratorKey.of(Integer.class, ConditionMethod.LT), IntegerStrategy.forMethod(ConditionMethod.LT),
                    GeneratorKey.of(Integer.class, ConditionMethod.GT), IntegerStrategy.forMethod(ConditionMethod.GT),
                    GeneratorKey.of(Long.class, ConditionMethod.EQ), LongStrategy.forMethod(ConditionMethod.EQ),
                    GeneratorKey.of(Long.class, ConditionMethod.NEQ), LongStrategy.forMethod(ConditionMethod.NEQ),
                    GeneratorKey.of(Long.class, ConditionMethod.LT), LongStrategy.forMethod(ConditionMethod.LT),
                    GeneratorKey.of(Long.class, ConditionMethod.GT), LongStrategy.forMethod(ConditionMethod.GT)
            ).toJavaMap();

    private TypedPropertyCompilerProvider() {
    }
}
