package net.ninjacat.omg.sql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.PatternException;
import net.ninjacat.omg.errors.TypeConversionException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TypedSqlParserTest {

    @Test
    public void shouldAllowShortList() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where age in (25, 35, 45)");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").in(io.vavr.collection.List.of(25, 35, 45).asJava())
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowIntToInt() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where id <> 11");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("id").neq(11)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowStringToString() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where name ~= 'John.*'");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("name").regex("John.*")
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowIntToDoubleConversion() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where height > 25");
        final Condition condition = sqlParser.getCondition();

        final Condition expected = Conditions.matcher()
                .property("height").gt(25)
                .build();

        assertThat(condition, is(expected));
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleToInt() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where id > 25.1");
        sqlParser.getCondition();
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleToString() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where name = 25.1");
        sqlParser.getCondition();
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleToStringList() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where name in ('a', 'b', 25.1)");
        sqlParser.getCondition();
    }


    @Test(expected = PatternException.class)
    public void shouldFailOnInvalidProperty() {
        final SqlParser sqlParser = SqlParser.of("select name, age from " + Data.class.getName() + " where unknown = 12");
        sqlParser.getCondition();
    }

    public static class Data {
        private final int id;
        private final double height;
        private final Short age;
        private final String name;

        public Data(final int id, final double height, final String name, final short age) {
            this.id = id;
            this.height = height;
            this.name = name;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public double getHeight() {
            return height;
        }

        public String getName() {
            return name;
        }

        public Short getAge() {
            return age;
        }
    }
}
