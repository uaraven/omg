package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;

import java.lang.reflect.Method;

import static net.ninjacat.omg.sql.SqlTypeConversion.toJavaTypeStrict;

public class ClassValidator implements TypeValidator {

    private final Class<?> clazz;

    ClassValidator(final Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object validate(final String fieldName, final String value) throws TypeConversionException {
        final Method callable = Reflect.getCallable(fieldName, clazz);
        final Class<?> propertyType = callable.getReturnType();

        return toJavaTypeStrict(propertyType, value);
    }

}
