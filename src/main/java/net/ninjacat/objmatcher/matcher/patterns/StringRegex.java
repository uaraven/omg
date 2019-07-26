package net.ninjacat.objmatcher.matcher.patterns;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Immutable
public class StringRegex implements Matcher<String> {
    private final Predicate<String> regexPredicate;
    private final String regex;

    StringRegex(final @NonNull String regex) {
        this.regex = regex;
        final Pattern pattern = Pattern.compile(regex);
        this.regexPredicate = (text) -> pattern.matcher(text).matches();
    }

    @Override
    public boolean matches(final String checkedValue) {
        return this.regexPredicate.test(checkedValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StringRegex that = (StringRegex) o;
        return regex.equals(that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex);
    }

    @Override
    public String toString() {
        return "~= /" + regex + '/';
    }
}
