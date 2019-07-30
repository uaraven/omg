package net.ninjacat.omg.conditions;

/**
 * Basic interface for condition.
 *
 * Conditions are simple criteria comparing named field to a value.
 */
public interface Condition {
    /**
     * Condition representation for pretty printing
     *
     * @param level Depth level
     * @return String representation of condition
     */
    String repr(int level);

    /**
     * Default representation without indentation
     *
     * @return String representation of condition
     */
    default String repr() {
        return repr(0);
    }

}
