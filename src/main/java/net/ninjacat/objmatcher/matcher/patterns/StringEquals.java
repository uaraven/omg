package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class StringEquals extends FieldPattern<String> {

    StringEquals(final String fieldName, final String value) {
        super(fieldName, value);
    }

    @Override
    public boolean matches(final String checkedValue) {
        return getValue().equals(checkedValue);
    }
}
