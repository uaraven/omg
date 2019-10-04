package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;

public class AlwaysTrueCondition implements Condition {
    public static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();

    @Override
    public String repr(final int level) {
        return Strings.indent("", level * 2) + "true";
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.EQ;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof AlwaysTrueCondition;
    }
}
