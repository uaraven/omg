package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;

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
        return Strings.indent("", level * 2) + property + " matches\n" + value.repr(level + 1);
    }

    @Override
    public String toString() {
        return repr();
    }
}