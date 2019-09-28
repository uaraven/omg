package net.ninjacat.omg.omql;

import net.ninjacat.omg.errors.TypeConversionException;

@FunctionalInterface
public interface TypeValidator {
    Object validate(String fieldName, String value) throws TypeConversionException;
}
