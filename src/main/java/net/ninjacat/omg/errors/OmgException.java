package net.ninjacat.omg.errors;

public class OmgException extends RuntimeException {

    protected OmgException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    protected OmgException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
