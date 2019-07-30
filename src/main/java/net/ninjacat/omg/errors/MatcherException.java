package net.ninjacat.omg.errors;

public class MatcherException extends RuntimeException {

    public MatcherException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public MatcherException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
