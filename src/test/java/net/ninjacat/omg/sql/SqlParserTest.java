package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.SqlParsingException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SqlParserTest {

    @Test
    public void shouldConvertSimpleQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age > 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").gt(25)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertGteQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age >= 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .or(c -> c
                        .property("age").gt(25)
                        .property("age").eq(25)
                )
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertLteQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age <= 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .or(c -> c
                        .property("age").lt(25)
                        .property("age").eq(25)
                )
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertNeqQuery1() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age <> 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").neq(25)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertNeqQuery2() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age != 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").neq(25)
                .build();

        assertThat(condition, is(expected));
    }


    @Test
    public void shouldConvertSimpleAndQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age < 25 and name = 'Iñigo'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .and(c -> c
                        .property("age").lt(25)
                        .property("name").eq("Iñigo"))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleOrQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age > 25 or name = 'Iñigo'");
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
        final SqlParser sqlParser = SqlParser.of("select name, age where age > 25 or name = 'Iñigo' and status=\"Searching\"");
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
        final SqlParser sqlParser = SqlParser.of("select name, age where age > 25 and name = 'Iñigo' and status=\"Searching\"");
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
        final SqlParser sqlParser = SqlParser.of("select name, age where name ~= '^Iñigo.*'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("name").regex("^Iñigo.*")
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertInQuery() {
        final SqlParser sqlParser = SqlParser.of("select name, age where age in (1,2,3)");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").in(io.vavr.collection.List.of(1, 2, 3).asJava())
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSubQuery() {
        final SqlParser sqlParser = SqlParser.of("select * where friend in (select * where age > 18)");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("friend").match(m -> m
                        .property("age").gt(18))
                .build();

        assertThat(condition, is(expected));
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailToParseQuery() {
        final SqlParser sqlParser = SqlParser.of("select * where friend is unlike others");
        sqlParser.getCondition();
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailWhenInContainsDifferentTypes() {
        final SqlParser sqlParser = SqlParser.of("select * where age in (21, 22, 'old enough')");
        sqlParser.getCondition();
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailWhenRegexOnNonString() {
        final SqlParser sqlParser = SqlParser.of("select * where age ~= 20");
        sqlParser.getCondition();
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailOnSyntaxError1() {
        final SqlParser sqlParser = SqlParser.of("select * where name = 'jack");
        sqlParser.getCondition();
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailOnSyntaxError2() {
        final SqlParser sqlParser = SqlParser.of("select * where name in ('jack', 'john'");
        sqlParser.getCondition();
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailOnSyntaxError3() {
        final SqlParser sqlParser = SqlParser.of("select * where name in (not a list or subquery)");
        sqlParser.getCondition();
    }
}
