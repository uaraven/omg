package net.ninjacat.omg.conditions;

public class RegexCondition extends ComparisonCondition<String> {
    public RegexCondition(final String property, final String value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "~=";
    }


}
