package net.ninjacat.objmatcher.matcher.patterns;


public abstract class FieldPattern<T> {
    private final String fieldName;
    private final Class fieldType;
    private final T value;

    protected FieldPattern(final String fieldName, final Class fieldType, final T value) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.value = value;
    }

    public abstract boolean matches(final T checkedValue);

    public Class getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getValue() {
        return value;
    }
}
