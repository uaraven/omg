package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.AlwaysTrueCondition;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.errors.TypeConversionException;
import org.immutables.value.Value;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueryCompilerTest {

    private static final List<Class<?>> SOURCES = io.vavr.collection.List.of(
            Person.class,
            JetPilot.class
    ).asJava();

    @Test
    public void shouldConvertSimpleQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age > 25", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").gt(25)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertGteQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select callsign, age from JetPilot where age >= 25", SOURCES);
        final Condition condition = queryCompiler.getCondition();

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
        final QueryCompiler queryCompiler = QueryCompiler.of("select callsign, age from JetPilot where age <= 25", SOURCES);
        final Condition condition = queryCompiler.getCondition();

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
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age <> 25", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").neq(25)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertNeqQuery2() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age != 25", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").neq(25)
                .build();

        assertThat(condition, is(expected));
    }


    @Test
    public void shouldConvertSimpleAndQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age < 25 and name = 'Iñigo'", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .and(c -> c
                        .property("age").lt(25)
                        .property("name").eq("Iñigo"))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleOrQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age > 25 or name = 'Iñigo'", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .or(c -> c
                        .property("age").gt(25)
                        .property("name").eq("Iñigo"))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSimpleAndOrQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age > 25 or name = 'Iñigo' and status=\"Searching\"", SOURCES);
        final Condition condition = queryCompiler.getCondition();

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
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age > 25 and name = 'Iñigo' and status=\"Searching\"", SOURCES);
        final Condition condition = queryCompiler.getCondition();

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
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where name ~= '^Iñigo.*'", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("name").regex("^Iñigo.*")
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertInQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person where age in (1,2,3)", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").in(io.vavr.collection.List.of(1, 2, 3).asJava())
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldConvertSubQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where friend in (select * from Person where age > 18)", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("friend").match(m -> m
                        .property("age").gt(18))
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldParseQueryWithoutWhere() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Person", SOURCES);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = AlwaysTrueCondition.INSTANCE;

        assertThat(condition, is(expected));
    }


    @Test(expected = OmqlParsingException.class)
    public void shouldFailToParseQuery() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where friend is unlike others", SOURCES);
        queryCompiler.getCondition();
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailWhenInContainsDifferentTypes() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where age in (21, 22, 'old enough')", SOURCES);
        queryCompiler.getCondition();
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailWhenRegexOnNonString() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where age ~= 20", SOURCES);
        queryCompiler.getCondition();
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailOnSyntaxError1() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where name = 'jack", SOURCES);
        queryCompiler.getCondition();
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailOnSyntaxError2() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where name in ('jack', 'john'", SOURCES);
        queryCompiler.getCondition();
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailOnSyntaxError3() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Person where name in (not a list or subquery)", SOURCES);
        queryCompiler.getCondition();
    }

    @Value.Immutable
    public interface Person {
        int age();

        String name();

        String status();
    }

    @Value.Immutable
    public interface JetPilot {
        int age();

        String callsign();
    }
}
