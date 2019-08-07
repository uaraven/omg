package net.ninjacat.omg.bytecode;

/**
 * Order in which operands are pushed onto stack for comparison
 */
public enum CompareOrdering {
    PROPERTY_THEN_MATCHING,
    MATCHING_THEN_PROPERTY;
}
