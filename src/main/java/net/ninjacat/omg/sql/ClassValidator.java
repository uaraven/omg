package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;
import net.ninjacat.omg.utils.TypeUtils;

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

    private <T> void failIfNotAssignable(final T value, final Class<?> propertyType) {
        final Class<?> widenedPropertyType = TypeUtils.widen(propertyType);
        final Class<?> widenedValueType = TypeUtils.widen(value.getClass());
        if (!(widenedPropertyType.equals(Double.class) && widenedValueType.equals(Long.class))) {
            // Allow int to double checks, but not vice versa
            if (!widenedPropertyType.isAssignableFrom(widenedValueType)) {
                throw new TypeConversionException(value.getClass(), value, propertyType);
            }
        }
    }
}
