package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonCondition)) return false;
        final ComparisonCondition<?> that = (ComparisonCondition<?>) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, getValue());
    }
}
