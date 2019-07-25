package net.ninjacat.objmatcher.matcher.patterns;


public abstract class FieldPattern<T> {
    private final String fieldName;
    private final T value;

    protected FieldPattern(final String fieldName, final T value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public abstract boolean matches(final T checkedValue);

    public String getFieldName() {
        return fieldName;
    }

    public T getValue() {
        return value;
    }
}
