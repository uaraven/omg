package net.ninjacat.omg.errors;

public class PatternException extends OmgException {

    public PatternException(final String format, final Object... args) {
        super(format, args);
    }

    public PatternException(final Throwable cause, final String format, final Object... args) {
        super(cause, format, args);
    }
}
