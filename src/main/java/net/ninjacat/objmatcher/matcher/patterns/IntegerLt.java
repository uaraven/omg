package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerLt extends FieldPattern<Integer> {
    IntegerLt(final String fieldName, final Integer value) {
        super(fieldName, Integer.class, value);
    }

    @Override
    public boolean matches(final Integer checkedValue) {
        return checkedValue.compareTo(getValue()) < 0;
    }
}
