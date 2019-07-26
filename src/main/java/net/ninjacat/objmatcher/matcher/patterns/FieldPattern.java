package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;
import net.jcip.annotations.Immutable;

@Value
@Immutable
public class FieldPattern<T> implements Matcher<T> {
    String fieldName;
    Class fieldType;
    Matcher<T> matcher;

    public boolean matches(final T checkedValue) {
        return getMatcher().matches(checkedValue);
    }

}
