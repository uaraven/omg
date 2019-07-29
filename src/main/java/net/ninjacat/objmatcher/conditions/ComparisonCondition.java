package net.ninjacat.objmatcher.conditions;

import net.ninjacat.objmatcher.utils.Strings;

public abstract class ComparisonCondition<T> implements PropertyCondition<T> {
    private final String field;
    private final T value;

    ComparisonCondition(final String property, final T value) {
        this.field = property;
        this.value = value;
    }

    @Override
    public String getProperty() {
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

    @Override
    public String repr(final int level) {
        return Strings.indent("", level * 2) + "'" + getProperty() + "' " + operatorRepr() + " '" + getValue() + "'";
    }

    protected abstract String operatorRepr();
}
