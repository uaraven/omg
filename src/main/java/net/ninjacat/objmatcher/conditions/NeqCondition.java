package net.ninjacat.objmatcher.conditions;

public class NeqCondition<T> extends ComparisonCondition<T> {
    public NeqCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "!=";
    }

}
