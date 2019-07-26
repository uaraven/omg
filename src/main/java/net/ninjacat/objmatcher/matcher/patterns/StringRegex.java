package net.ninjacat.objmatcher.matcher.patterns;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Value
@EqualsAndHashCode(callSuper = true)
public class StringRegex extends FieldPattern<String> {
    private final Predicate<String> regexPredicate;

    StringRegex(final String fieldName, final String regex) {
        super(fieldName, String.class, regex);
        final Pattern pattern = Pattern.compile(regex);
        this.regexPredicate = (text) -> pattern.matcher(text).matches();
    }

    @Override
    public boolean matches(final String checkedValue) {
        return this.regexPredicate.test(checkedValue);
    }
}
