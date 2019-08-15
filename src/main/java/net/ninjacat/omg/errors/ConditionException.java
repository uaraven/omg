package net.ninjacat.omg.errors;

public class ConditionException extends OmgException {
    public ConditionException(final Throwable cause, final String format, final Object... args) {
        super(cause, "Condition error: %s", String.format(format, args));
    }

    public ConditionException(final String format, final Object... args) {
        super("Condition error: %s", String.format(format, args));
    }
}
