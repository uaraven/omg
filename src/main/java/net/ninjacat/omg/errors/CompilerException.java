package net.ninjacat.omg.errors;

public class CompilerException extends RuntimeException {

    public CompilerException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public CompilerException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
