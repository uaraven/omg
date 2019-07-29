package net.ninjacat.objmatcher.matcher.conditions;

import net.ninjacat.objmatcher.utils.Strings;

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

    @Override
    public String repr(int level) {
        return Strings.indent("", level * 2) + "'" + getField() + "' " + operatorRepr() + " '" + getValue() + "'";
    }

    protected abstract String operatorRepr();
}
