package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;

public class FakeValidator implements TypeValidator {
    @Override
    public <T> void validate(final String fieldName, final T value) throws TypeConversionException {
        // accept every value
    }
}
