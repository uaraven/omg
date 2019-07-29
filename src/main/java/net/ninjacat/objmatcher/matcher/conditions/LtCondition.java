package net.ninjacat.objmatcher.matcher.conditions;

public class LtCondition<T> extends ComparisonCondition<T> {
    public LtCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    public String repr(final int level) {
        return "'" + getField() + "' < '" + getValue() + "'";
    }
}
