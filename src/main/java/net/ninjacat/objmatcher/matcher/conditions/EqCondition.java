package net.ninjacat.objmatcher.matcher.conditions;

public class EqCondition<T> extends ComparisonCondition<T> {
    public EqCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return "==";
    }
}
