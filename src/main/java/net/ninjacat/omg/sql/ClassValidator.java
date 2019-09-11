package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;

import java.lang.reflect.Method;

public class ClassValidator implements TypeValidator {

    private final Class<?> clazz;

    ClassValidator(final Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public <T> void validate(final String fieldName, final T value) throws TypeConversionException {
        final Method callable = Reflect.getCallable(fieldName, clazz);
        final Class<?> propertyType = callable.getReturnType();

        failIfNotAssignable(value, propertyType);
    }

    private <T> void failIfNotAssignable(T value, Class<?> propertyType) {

    }
}
