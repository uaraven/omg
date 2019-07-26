package net.ninjacat.objmatcher.matcher;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;
import net.ninjacat.objmatcher.matcher.patterns.Matchers;
import org.junit.Test;

import static net.ninjacat.objmatcher.matcher.patterns.Matchers.integer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class SlowObjectMatcherTest {

    @Test
    public void shouldMatchObject() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestValue.class.getName())
                .fieldMatcher(Matchers.string("field1").equalTo("test1"))
                .fieldMatcher(Matchers.string("field2").notEqualTo("test2"))
                .build();

        final TestValue test = new TestValue("test1", "test3");

        final SlowObjectMatcher<TestValue> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);
        final boolean matches = slowObjectMatcher.matches(test);

        assertThat(matches, org.hamcrest.Matchers.is(true));
    }

    @Test
    public void shouldNotMatchObjectWhenFieldIsDifferent() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestValue.class.getName())
                .fieldMatcher(Matchers.string("field1").equalTo("test1"))
                .fieldMatcher(Matchers.string("field2").notEqualTo("test3"))
                .build();

        final TestValue test = new TestValue("test1", "test3");

        final SlowObjectMatcher<TestValue> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);
        final boolean matches = slowObjectMatcher.matches(test);

        assertThat(matches, org.hamcrest.Matchers.is(false));
    }

    @Test
    public void shouldNotMatchObjectWhenClassIsDifferent() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestValue.class.getName())
                .fieldMatcher(Matchers.string("field1").equalTo("test1"))
                .fieldMatcher(Matchers.string("field2").notEqualTo("test3"))
                .build();

        final NotTestValue test = new NotTestValue("test1", "test2");

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);
        final boolean matches = slowObjectMatcher.matches(test);

        assertThat(matches, org.hamcrest.Matchers.is(false));
    }

    @Test
    public void shouldFilterMatching() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestValue.class.getName())
                .fieldMatcher(Matchers.string("field1").equalTo("test1"))
                .fieldMatcher(Matchers.string("field2").notEqualTo("failed"))
                .build();

        final List<Object> list = List.of(
                new TestValue("test1", "success"),
                new TestValue("test1", "failed"),
                new TestValue("test2", "failed"),
                new NotTestValue("test1", "success"),
                new TestValue("test1", "success")
        );

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);

        final List<Object> results = list.toStream().filter(slowObjectMatcher::matches).toList();

        assertThat(results.asJava(), hasSize(2));
        assertThat(results, containsInAnyOrder(new TestValue("test1", "success"), new TestValue("test1", "success")));
    }

    @Test
    public void shouldMatchNonIntField() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestInts.class.getName())
                .fieldMatcher(integer("shortField").equalTo(11))
                .build();

        final TestInts test = new TestInts((short) 11);

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);
        final boolean matches = slowObjectMatcher.matches(test);

        assertThat(matches, org.hamcrest.Matchers.is(true));
    }

    @Test
    public void shouldFilterGtThanTen() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(TestInts.class.getName())
                .fieldMatcher(integer("shortField").greaterThan(10))
                .build();

        final List<TestInts> list = List.of(
                new TestInts((short) 1),
                new TestInts((short) 15),
                new TestInts((short) 10),
                new TestInts((short) 123),
                new TestInts((short) -5)
        );

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);

        final List<TestInts> results = list.toStream().filter(slowObjectMatcher::matches).toList();

        assertThat(results.asJava(), hasSize(2));
        assertThat(results, containsInAnyOrder(new TestInts((short) 15), new TestInts((short) 123)));
    }

    @Test
    public void shouldFilterWithLogicalFilters() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className(LogicalTest.class.getName())
                .fieldMatcher(
                        Matchers.integer("intField").or()..done()
                        )

                .build();

        final List<TestInts> list = List.of(
                new TestInts((short) 1),
                new TestInts((short) 15),
                new TestInts((short) 10),
                new TestInts((short) 123),
                new TestInts((short) -5)
        );

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.forPattern(pattern);

        final List<TestInts> results = list.toStream().filter(slowObjectMatcher::matches).toList();

        assertThat(results.asJava(), hasSize(2));
        assertThat(results, containsInAnyOrder(new TestInts((short) 15), new TestInts((short) 123)));
    }

    @Value
    private static class LogicalTest {
        String strField;
        int intField;
    }

}