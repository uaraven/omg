package net.ninjacat.omg.omql;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SyntaxError {
    abstract String message();

    abstract int line();

    abstract int position();

    abstract Object offendingSymbol();

    @Override
    public String toString() {
        return String.format("[%d : %d] %s", line(), position(), message());
    }
}
