package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SqlParserTest {
    @Test
    public void shouldConvertSimpleQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age > 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").gt(25)
                .build();

        assertThat(condition, is(expected));
    }
}