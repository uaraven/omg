package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class StringNotEq extends FieldPattern<String> {

    StringNotEq(final String fieldName, final String value) {
        super(fieldName, String.class, value);
    }

    @Override
    public boolean matches(final String checkedValue) {
        return !getValue().equals(checkedValue);
    }

}
