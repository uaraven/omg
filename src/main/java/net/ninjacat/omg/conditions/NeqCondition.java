package net.ninjacat.omg.conditions;

public class NeqCondition<T> extends ComparisonCondition<T> {
    NeqCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "!=";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.NEQ;
    }
}
