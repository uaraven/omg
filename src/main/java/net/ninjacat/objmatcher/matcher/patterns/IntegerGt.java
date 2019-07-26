package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerGt extends FieldPattern<Long> {

    IntegerGt(final String fieldName, final Long value) {
        super(fieldName, Long.class, value);
    }

    @Override
    public boolean matches(final Long checkedValue) {
        return checkedValue.compareTo(getValue()) > 0;
    }
}
