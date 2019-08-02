package net.ninjacat.omg.errors;

public class CompilerException extends OmgException {

    public CompilerException(final String format, final Object... args) {
        super(format, args);
    }

    public CompilerException(final Throwable cause, final String format, final Object... args) {
        super(cause, format, args);
    }
}
