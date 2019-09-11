package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;

public interface TypeValidator {
    <T> void validate(String fieldName, T value) throws TypeConversionException;
}
