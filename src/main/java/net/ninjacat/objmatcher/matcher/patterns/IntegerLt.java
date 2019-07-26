package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class IntegerLt extends FieldPattern<Long> {
    IntegerLt(final String fieldName, final Long value) {
        super(fieldName, Long.class, value);
    }

    @Override
    public boolean matches(final Long checkedValue) {
        return checkedValue.compareTo(getValue()) < 0;
    }
}
