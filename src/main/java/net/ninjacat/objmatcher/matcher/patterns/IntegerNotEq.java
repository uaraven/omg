package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerNotEq extends FieldPattern<Integer> {
    IntegerNotEq(final String fieldName, final Integer value) {
        super(fieldName, Integer.class, value);
    }

    @Override
    public boolean matches(final Integer checkedValue) {
        return getValue().compareTo(checkedValue) != 0;
    }
}
