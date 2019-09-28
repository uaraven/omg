package net.ninjacat.omg.conditions;

import net.ninjacat.omg.errors.ConditionException;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class ConditionsTest {
    @Test(expected = ConditionException.class)
    public void shouldNotAllowMoreThanOneChildForNot() {
        Conditions.matcher().not(n -> n
                .property("a").lt(10)
                .property("b").eq(1)
        ).build();
    }

    @Test
    public void shouldIgnoreTypesAndOperations() {
        try {
            Conditions.matcher().property("stringProp").gt("some string").build();
        } catch (final Exception ex) {
            fail("Should not have failed");
        }
    }
}