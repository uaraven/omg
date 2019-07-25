package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerGt extends FieldPattern<Integer> {

    IntegerGt(final String fieldName, final Integer value) {
        super(fieldName, value);
    }

    @Override
    public boolean matches(final Integer checkedValue) {
        return checkedValue.compareTo(getValue()) > 0;
    }
}
