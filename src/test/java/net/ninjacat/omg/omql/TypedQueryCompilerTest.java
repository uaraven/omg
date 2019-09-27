package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.errors.PatternException;
import net.ninjacat.omg.errors.TypeConversionException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TypedQueryCompilerTest {

    @Test(expected = OmqlParsingException.class)
    public void shouldFailWhenNoFrom() {
        QueryCompiler.of("select name, age where age > 25", String.class);
    }

    @Test
    public void shouldAllowShortList() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where age in (25, 35, 45)", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("age").in(io.vavr.collection.List.of((short) 25, (short) 35, (short) 45).asJava())
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowIntToInt() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where id <> 11", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("id").neq(11)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowStringToString() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where name ~= 'John.*'", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("name").regex("John.*")
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowIntToDoubleConversion() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where height > 25", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("height").gt(25.0)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldAllowEnums() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where e ='E1'", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("e").eq(E.E1)
                .build();

        assertThat(condition, is(expected));
    }

    @Test
    public void shouldProcessObjectFields() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Data where sub IN (select * from Subclass where contents != 'A.*')", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Condition expected = Conditions.matcher()
                .property("sub").match(Conditions.matcher().property("contents").regex("A.*").build())
                .build();

        assertThat(condition, is(expected));
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleToString() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where name = 25.1", Data.class);
        queryCompiler.getCondition();
    }

    @Test(expected = TypeConversionException.class)
    public void shouldFailDoubleToStringList() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where name in ('a', 'b', 25.1)", Data.class);
        queryCompiler.getCondition();
    }


    @Test(expected = PatternException.class)
    public void shouldFailOnInvalidProperty() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from " + Data.class.getName() + " where unknown = 12", Data.class);
        queryCompiler.getCondition();
    }

    public enum E {
        E1,
        E2
    }

    public static class Subclass {
        private final String contents;

        Subclass(final String contents) {
            this.contents = contents;
        }

        public String getContents() {
            return contents;
        }
    }

    public static class Data {
        private final int id;
        private final double height;
        private final Short age;
        private final String name;
        private final E e;
        private final Subclass sub;

        public Data(final int id, final double height, final String name, final short age, final E e, final String content) {
            this.id = id;
            this.height = height;
            this.name = name;
            this.age = age;
            this.e = e;
            this.sub = new Subclass(content);
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

        public E getE() {
            return e;
        }

        public Subclass getSub() {
            return sub;
        }
    }
}
