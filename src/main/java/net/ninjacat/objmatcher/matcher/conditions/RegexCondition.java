package net.ninjacat.objmatcher.matcher.conditions;

public class RegexCondition extends ComparisonCondition<String> {
    public RegexCondition(final String property, final String value) {
        super(property, value);
    }

    @Override
    public String repr(final int level) {
        return "'" + getField() + "' ~= /" + getValue() + "/";
    }
}
