package net.ninjacat.omg.errors;

public class OmqlSecurityException extends OmgException {

    public OmqlSecurityException(final String format, final Object... args) {
        super(String.format(format, args));
    }
}
