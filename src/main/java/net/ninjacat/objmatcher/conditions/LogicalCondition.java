package net.ninjacat.objmatcher.conditions;

import java.util.List;

public interface LogicalCondition extends Condition {
    /**
     * List of children condition
     *
     * @return The list of conditions combined by logical operation
     */
    List<Condition> getChildren();

}
