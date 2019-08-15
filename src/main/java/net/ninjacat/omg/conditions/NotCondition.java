package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;
import org.immutables.value.Value;

@Value.Immutable
public abstract class NotCondition implements Condition {
    public abstract Condition getChild();

    @Override
    public String repr(final int level) {
        final StringBuilder sb = new StringBuilder(Strings.indent("NOT ", level * 2));
        sb.append(getChild().repr(level + 1)).append("\n");
        return sb.toString();
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
