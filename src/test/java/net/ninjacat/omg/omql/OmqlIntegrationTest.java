package net.ninjacat.omg.omql;

import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class OmqlIntegrationTest {

    @Test
    public void shouldHandleEnumsInTypedQueries() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select name, age from Data where e ='E1'", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Pattern pattern = Patterns.compile(
                condition,
                PatternCompiler.forClass(Data.class));

        final List<Data> test = io.vavr.collection.List.of(
                new Data(1, 5.5, "test 1", (short) 30, E.E1),
                new Data(2, 6.5, "test 2", (short) 20, E.E2)
        ).asJava();

        final List<Data> result = test.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(result, hasItems(new Data(1, 5.5, "test 1", (short) 30, E.E1)));
    }

    public enum E {
        E1,
        E2
    }

    @SuppressWarnings("WeakerAccess")
    public static class Data {
        private final int id;
        private final double height;
        private final Short age;
        private final String name;
        private final E e;

        public Data(final int id, final double height, final String name, final short age, final E e) {
            this.id = id;
            this.height = height;
            this.name = name;
            this.age = age;
            this.e = e;
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

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Data)) return false;
            final Data data = (Data) o;
            return getId() == data.getId() &&
                    Double.compare(data.getHeight(), getHeight()) == 0 &&
                    Objects.equals(getAge(), data.getAge()) &&
                    Objects.equals(getName(), data.getName()) &&
                    getE() == data.getE();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getHeight(), getAge(), getName(), getE());
        }
    }
}
