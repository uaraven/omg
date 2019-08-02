package net.ninjacat.omg.conditions;

public class EqCondition<T> extends ComparisonCondition<T> {
    EqCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "==";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.EQ;
    }


}
