package net.ninjacat.omg.omql;

import net.ninjacat.omg.errors.TypeConversionException;

public interface TypeValidator {
    Class<?> getReturnType(final String fieldName);

    boolean isObjectProperty(String fieldName);
    Object validate(String fieldName, String value) throws TypeConversionException;
}
