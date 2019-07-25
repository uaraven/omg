package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerEq extends FieldPattern<Integer> {
    IntegerEq(final String fieldName, final Integer value) {
        super(fieldName, value);
    }

    @Override
    public boolean matches(final Integer checkedValue) {
        return getValue().compareTo(checkedValue) == 0;
    }
}
