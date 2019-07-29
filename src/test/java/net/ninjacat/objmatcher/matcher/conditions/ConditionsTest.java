package net.ninjacat.objmatcher.matcher.conditions;

import org.junit.Test;

public class ConditionsTest {
    @Test
    public void name() {
        final Condition condition = Conditions.start()
                .property("test1").eq(1)
                .property("test2").gt(10)
                .or(orBuilder ->
                        orBuilder
                                .property("test3").eq(15)
                                .property("test3").gt(15))
                .build();
        System.out.println(condition.repr(0));
    }
}