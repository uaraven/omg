package net.ninjacat.objmatcher.matcher.errors;

public class PatternException extends RuntimeException {

    public PatternException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public PatternException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
