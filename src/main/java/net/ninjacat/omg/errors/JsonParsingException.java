package net.ninjacat.omg.errors;

public class JsonParsingException extends RuntimeException {

    public JsonParsingException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public JsonParsingException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
