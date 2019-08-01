package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.PatternCompilerStrategy;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.errors.CompilerException;

/**
 * Provider for choosing strategy for string matching based on ConditionMethod
 *
 * Strings support only EQ, NEQ and REGEX methods
 */
public class StringStrategyProvider {
    public static PatternCompilerStrategy forMethod(final ConditionMethod method) {
        switch (method) {
            case EQ:
            case NEQ:
                return new StringStrategy(method);
            case REGEX:
                return new StringRegexStrategy();
            default: throw new CompilerException("Unsupported condition '%s' for String type", method);
        }
    }
}
