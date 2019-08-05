package net.ninjacat.omg.errors;

public class TypeConversionException extends OmgException {

    public <T> TypeConversionException(final Class valueType, final T value, final Class targetType) {
        super("Cannot convert '%s %s' to '%s'", valueType.getName(), value, targetType.getName());
    }
}
