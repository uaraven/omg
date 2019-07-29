package net.ninjacat.objmatcher.reflect;


import net.jcip.annotations.Immutable;

import java.util.regex.Pattern;

@Immutable
public class StringRegexPattern<T> extends BaseStringPattern<T> {
    private final Pattern pattern;

    StringRegexPattern(final Property property, final String matchingValue) {
        super(property, matchingValue);
        this.pattern = Pattern.compile(matchingValue);
    }

    @Override
    protected boolean compare(final String propertyValue) {
        return pattern.matcher(propertyValue).matches();
    }

    @Override
    protected String getComparatorAsString() {
        return "~=";
    }
}
