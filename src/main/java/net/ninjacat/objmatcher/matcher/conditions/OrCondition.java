package net.ninjacat.objmatcher.matcher.conditions;

import lombok.Value;
import net.ninjacat.objmatcher.utils.Strings;

import java.util.List;

@Value
public class OrCondition implements LogicalCondition {
    private List<Condition> children;

    @Override
    public String repr(final int level) {
        final StringBuilder sb = new StringBuilder(Strings.indent("OR {", level * 2)).append("\n");
        children.forEach(item -> sb.append(item.repr(level + 1)).append("\n"));
        return sb.append(Strings.indent("}", level * 2)).append("\n").toString();
    }

    @Override
    public String toString() {
        return repr();
    }

}
