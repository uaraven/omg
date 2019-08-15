package net.ninjacat.omg.conditions;

import net.ninjacat.omg.errors.ConditionException;
import org.junit.Test;

public class ConditionsTest {
    @Test(expected = ConditionException.class)
    public void shouldNotAllowMoreThanOneChildForNot() {
        Conditions.matcher().not(n -> n
                .property("a").lt(10)
                .property("b").eq(1)
        ).build();
    }
}