package net.ninjacat.objmatcher.matcher.errors;

public class MatchingException extends RuntimeException {
    public MatchingException(final String message) {
        super(message);
    }

    public MatchingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
