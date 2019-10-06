package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.Patterns;
import org.immutables.value.Value;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class OmqlIntegrationTest {

    @Test
    public void shouldHandleEnumsInTypedQueries() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Data where e ='E1'", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Pattern<Data> pattern = Patterns.compile(
                condition,
                PatternCompiler.forClass(Data.class));

        final List<Data> test = io.vavr.collection.List.of(
                new Data(1, 5.5, "test 1", (short) 30, E.E1),
                new Data(2, 6.5, "test 2", (short) 20, E.E2)
        ).asJava();

        final List<Data> result = test.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(result, hasItems(new Data(1, 5.5, "test 1", (short) 30, E.E1)));
    }

    @Test
    public void shouldHandleNoWhere() {
        final QueryCompiler queryCompiler = QueryCompiler.of("select * from Data", Data.class);
        final Condition condition = queryCompiler.getCondition();

        final Pattern<Data> pattern = Patterns.compile(
                condition,
                PatternCompiler.forClass(Data.class));

        final List<Data> test = io.vavr.collection.List.of(
                new Data(1, 5.5, "test 1", (short) 30, E.E1),
                new Data(2, 6.5, "test 2", (short) 20, E.E2)
        ).asJava();

        final List<Data> result = test.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(result, hasItems(
                new Data(1, 5.5, "test 1", (short) 30, E.E1),
                new Data(2, 6.5, "test 2", (short) 20, E.E2)));
    }

    @Test
    public void shouldParseExampleFromDocs() {
        final QueryCompiler queryCompiler = QueryCompiler.of(
                "SELECT * FROM LivingPerson WHERE homeAddress.city.name = 'Windsor' OR (\n" +
                        "  homeAddress.city.name = 'Toronto' AND \n" +
                        "  homeAddress.city.districtName IN ('Etobicoke', 'Scarborough'))", LivingPerson.class);
        final Condition condition = queryCompiler.getCondition();

        final LivingPerson windsorPerson = ImmutableLivingPerson.of(ImmutableAddress.of(ImmutableCity.of("Windsor", "")));
        final LivingPerson scarboroPerson = ImmutableLivingPerson.of(ImmutableAddress.of(ImmutableCity.of("Toronto", "Scarborough")));
        final LivingPerson etobicokePerson = ImmutableLivingPerson.of(ImmutableAddress.of(ImmutableCity.of("Toronto", "Etobicoke")));
        final LivingPerson kitchenerPerson = ImmutableLivingPerson.of(ImmutableAddress.of(ImmutableCity.of("Kitchener", "")));

        final Pattern<LivingPerson> pattern = Patterns.compile(
                condition,
                PatternCompiler.forClass(LivingPerson.class));

        final List<LivingPerson> source = io.vavr.collection.List.of(windsorPerson, scarboroPerson, etobicokePerson, kitchenerPerson).asJava();

        final List<LivingPerson> result = source.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(result, hasItems(windsorPerson, scarboroPerson, etobicokePerson));
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

    @Value.Immutable
    public interface City {
        @Value.Parameter(order = 1)
        String name();

        @Value.Parameter(order = 2)
        String districtName();
    }

    @Value.Immutable
    public interface Address {
        @Value.Parameter
        City city();
    }

    @Value.Immutable
    public interface LivingPerson {
        @Value.Parameter
        Address homeAddress();
    }
}
