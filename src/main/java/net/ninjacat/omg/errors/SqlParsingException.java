package net.ninjacat.omg.errors;

public class SqlParsingException extends OmgException {

    public SqlParsingException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    public SqlParsingException(final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
    }
}
