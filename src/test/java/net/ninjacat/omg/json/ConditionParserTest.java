package net.ninjacat.omg.json;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.JsonParsingException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConditionParserTest {

    @Test
    public void shouldParseSimpleCondition() {
        final String json = Utils.getJson("simple-condition.json");
        final Condition parsed = ConditionParser.parse(json);
        final Condition prop = Conditions.matcher().property("testProp").eq(50).build();

        assertThat(parsed, is(prop));
    }

    @Test
    public void shouldParseMultipleCondition() {
        final String json = Utils.getJson("multi-condition.json");
        final Condition parsed = ConditionParser.parse(json);
        final Condition prop = Conditions.matcher()
                .property("testProp").eq(50)
                .property("strProp").regex(".*")
                .build();

        assertThat(parsed, is(prop));
    }


    @Test
    public void shouldParseOrMultipleCondition() {
        final String json = Utils.getJson("multi-condition-or.json");
        final Condition parsed = ConditionParser.parse(json);
        final Condition prop = Conditions.matcher()
                .or(cond -> cond
                        .property("testProp").eq(50)
                        .property("strProp").regex(".*")
                        .build())
                .build();

        assertThat(parsed, is(prop));
    }

    @Test
    public void shouldParseNestedCondition() {
        final String json = Utils.getJson("nested-condition.json");
        final Condition parsed = ConditionParser.parse(json);
        final Condition prop = Conditions.matcher()
                .property("testProp").eq(50)
                .property("nested").match(nested -> nested
                        .property("nestedStr").eq("123")
                        .property("nestedDbl").gt(12.3)
                        .build()
                )
                .build();

        assertThat(parsed, is(prop));
    }


    @Test(expected = JsonParsingException.class)
    public void shouldFailOnUnknownOp() {
        final String json = Utils.getJson("invalid-condition-1.json");
        ConditionParser.parse(json);
    }

    @Test(expected = JsonParsingException.class)
    public void shouldFailOnTypeMismatch() {
        final String json = Utils.getJson("invalid-condition-2.json");
        ConditionParser.parse(json);
    }
}