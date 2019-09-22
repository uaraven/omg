package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Provider for choosing strategy for objects matching based on ConditionMethod
 * <p>
 * Object support only EQ, NEQ and MATCH methods
 */
public final class ObjectStrategyProvider {
    private ObjectStrategyProvider() {
    }


    public static PatternCompilerStrategy forMethod(final Class<?> cls, final ConditionMethod method) {
        switch (method) {
            case EQ:
            case NEQ:
                return new ObjectStrategy(method);
            case MATCH:
                return new ObjectMatchStrategy();
            case IN:
                return new ReferenceInStrategy();
            case REGEX:
                return new ObjectRegexStrategy();
            default:
                throw new CompilerException("Unsupported condition '%s' for Object type", method);
        }
    }
}
