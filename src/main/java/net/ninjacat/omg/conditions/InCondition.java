package net.ninjacat.omg.conditions;

import java.util.List;

public class InCondition<T> extends ComparisonCondition<List<T>> {

    public InCondition(final String property, final List<T> value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "in";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.IN;
    }

}