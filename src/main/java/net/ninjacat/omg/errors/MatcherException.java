package net.ninjacat.omg.errors;

public class MatcherException extends OmgException {

    public MatcherException(final String format, final Object... args) {
        super(format, args);
    }

    public MatcherException(final Throwable cause, final String format, final Object... args) {
        super(cause, format, args);
    }
}
