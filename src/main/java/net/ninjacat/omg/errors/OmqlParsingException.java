package net.ninjacat.omg.errors;

public class OmqlParsingException extends OmgException {

    public OmqlParsingException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public OmqlParsingException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
