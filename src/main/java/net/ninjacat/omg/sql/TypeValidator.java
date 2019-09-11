package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;

public interface TypeValidator {
    void validate(String fieldName, String value) throws TypeConversionException;
}
