package net.ninjacat.objmatcher.matcher.conditions;

public abstract class ComparisonCondition<T> implements PropertyCondition<T> {
    private final String field;
    private final T value;

    ComparisonCondition(final String property, final T value) {
        this.field = property;
        this.value = value;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return repr();
    }

}
