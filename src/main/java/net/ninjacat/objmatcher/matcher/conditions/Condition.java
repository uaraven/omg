package net.ninjacat.objmatcher.matcher.conditions;

/**
 * Basic interface for condition
 */
public interface Condition {
    /**
     * Condition representation for pretty printing
     *
     * @param level Depth level
     * @return String representation of condition
     */
    String repr(int level);

    default String repr() {
        return repr(0);
    }

}
