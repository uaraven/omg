package net.ninjacat.omg.bytecode;

import lombok.Value;
import net.ninjacat.omg.conditions.ConditionMethod;

@Value
public class GeneratorKey {
    Class returnType;
    ConditionMethod conditionMethod;

    public static GeneratorKey of(final Class type, final ConditionMethod conditionMethod) {
        return new GeneratorKey(type, conditionMethod);
    }
}
