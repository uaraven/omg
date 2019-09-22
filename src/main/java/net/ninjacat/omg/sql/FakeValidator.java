package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.TypeConversionException;

import static net.ninjacat.omg.sql.SqlTypeConversion.toJavaType;

public class FakeValidator implements TypeValidator {
    @Override
    public Object validate(final String fieldName, final String value) throws TypeConversionException {
        return toJavaType(value);
    }
}
