package net.ninjacat.omg.omql;

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;

import java.lang.reflect.Method;

import static net.ninjacat.omg.omql.OmqlTypeConversion.toJavaTypeStrict;

public class ClassValidator implements TypeValidator {

    private final Class<?> clazz;

    ClassValidator(final Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object validate(final String fieldName, final String value) throws TypeConversionException {
        final Class<?> propertyType = getReturnType(fieldName);

        return toJavaTypeStrict(propertyType, value);
    }

    @Override
    public boolean isObjectProperty(final String fieldName) {
        return !getReturnType(fieldName).isPrimitive();
    }

    @Override
    public Class<?> getReturnType(final String fieldName) {
        final Method callable = Reflect.getCallable(fieldName, clazz);
        return callable.getReturnType();
    }

}
