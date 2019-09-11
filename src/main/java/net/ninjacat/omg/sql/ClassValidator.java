package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;

import java.lang.reflect.Method;

public class ClassValidator implements TypeValidator {

    private Class<?> clazz;

    ClassValidator(final Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void validate(final String fieldName, final String value) throws TypeConversionException {
        final Method callable = Reflect.getCallable(fieldName, clazz);
        callable.getReturnType();
    }
}
