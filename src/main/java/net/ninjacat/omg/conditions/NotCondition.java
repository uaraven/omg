package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;
import org.immutables.value.Value;

@Value.Immutable
public abstract class NotCondition implements Condition {
    public abstract Condition getChild();

    @Override
    public String repr(final int level) {
        return Strings.indent("NOT ", level * 2) + getChild().repr(0) + "\n";
    }

    @Override
    public String toString() {
        return repr();
    }

    @Override
    public ConditionMethod getMethod() {
        return ConditionMethod.LOGIC;
    }
}
