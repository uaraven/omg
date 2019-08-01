package net.ninjacat.omg.conditions;

public class LtCondition<T> extends ComparisonCondition<T> {
    LtCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "<";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.LT;
    }
}
