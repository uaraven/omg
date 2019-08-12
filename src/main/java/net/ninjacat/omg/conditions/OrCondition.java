package net.ninjacat.omg.conditions;

import net.ninjacat.omg.utils.Strings;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class OrCondition implements LogicalCondition {
    @Override
    public abstract List<Condition> getChildren();

    @Override
    public String repr(final int level) {
        final StringBuilder sb = new StringBuilder(Strings.indent("OR {", level * 2)).append("\n");
        getChildren().forEach(item -> sb.append(item.repr(level + 1)).append("\n"));
        return sb.append(Strings.indent("}", level * 2)).append("\n").toString();
    }

    @Override
    public String toString() {
        return repr();
    }

}
