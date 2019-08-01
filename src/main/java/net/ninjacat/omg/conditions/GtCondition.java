package net.ninjacat.omg.conditions;

public class GtCondition<T> extends ComparisonCondition<T> {
    GtCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return ">";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.GT;
    }
}
