package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerNotEq extends FieldPattern<Long> {
    IntegerNotEq(final String fieldName, final Long value) {
        super(fieldName, Long.class, value);
    }

    @Override
    public boolean matches(final Long checkedValue) {
        return getValue().compareTo(checkedValue) != 0;
    }
}
