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

    @Test
    public void shouldConvertSimpleAndQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age > 25 and name = 'Iñigo'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .and(c -> c
                        .property("age").gt(25)
                        .property("name").eq("Iñigo"))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleOrQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age > 25 or name = 'Iñigo'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .or(c -> c
                        .property("age").gt(25)
                        .property("name").eq("Iñigo"))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleAndOrQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age > 25 or name = 'Iñigo' and status=\"Searching\"");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .or(c -> c
                        .property("age").gt(25)
                        .and(a -> a
                                .property("name").eq("Iñigo")
                                .property("status").eq("Searching")
                        )
                )
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertMultiAndQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age > 25 and name = 'Iñigo' and status=\"Searching\"");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .and(c -> c
                        .and(a -> a
                                .property("age").gt(25)
                                .property("name").eq("Iñigo")
                        )
                        .property("status").eq("Searching")
                )
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleRegexQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where name ~= '^Iñigo.*'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("name").regex("^Iñigo.*")
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertInQuery() {
        final SqlParser sqlParser = new SqlParser("select name, age from data where age in (1,2,3)");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").in(io.vavr.collection.List.of(1, 2, 3).asJava())
                .build();

        assertThat(condition, is(expected));
    }

}
