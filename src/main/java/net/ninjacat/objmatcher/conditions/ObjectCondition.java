package net.ninjacat.objmatcher.conditions;

public class ObjectCondition implements PropertyCondition<Condition> {
    private final String property;
    private final Condition value;

    ObjectCondition(final String property, final Condition value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public Condition getValue() {
        return value;
    }

    @Override
    public String repr(final int level) {
        return property + " matches\n" + value.repr(level);
    }
}
