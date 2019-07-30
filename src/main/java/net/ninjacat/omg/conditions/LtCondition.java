package net.ninjacat.omg.conditions;

public class LtCondition<T> extends ComparisonCondition<T> {
    public LtCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "<";
    }
}
