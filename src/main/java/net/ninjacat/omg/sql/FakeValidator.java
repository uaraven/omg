package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;

public class FakeValidator implements TypeValidator {
    @Override
    public void validate(final String fieldName, final String value) throws TypeConversionException {
        // accept every value
    }
}
