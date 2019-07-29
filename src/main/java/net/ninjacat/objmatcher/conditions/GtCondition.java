package net.ninjacat.objmatcher.conditions;

public class GtCondition<T> extends ComparisonCondition<T> {
    public GtCondition(final String property, final T value) {
        super(property, value);
    }

    @Override
    protected String operatorRepr() {
        return ">";
    }


}
