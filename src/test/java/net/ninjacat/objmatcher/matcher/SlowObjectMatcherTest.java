package net.ninjacat.objmatcher.matcher;

import net.ninjacat.objmatcher.matcher.patterns.ObjectPattern;
import net.ninjacat.objmatcher.matcher.patterns.Patterns;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class SlowObjectMatcherTest {

    @Test
    public void shouldMatchObject() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className("TestValue")
                .fieldMatcher(Patterns.string("field1").equalTo("test1"))
                .fieldMatcher(Patterns.string("field2").notEqualTo("test2"))
                .build();

        final TestValue test = new TestValue("test1", "test3");

        final SlowObjectMatcher<TestValue> slowObjectMatcher = SlowObjectMatcher.create();
        final boolean matches = slowObjectMatcher.matches(test, pattern);

        assertThat(matches, Matchers.is(true));
    }

    @Test
    public void shouldNotMatchObjectWhenFieldIsDifferent() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className("TestValue")
                .fieldMatcher(Patterns.string("field1").equalTo("test1"))
                .fieldMatcher(Patterns.string("field2").notEqualTo("test3"))
                .build();

        final TestValue test = new TestValue("test1", "test3");

        final SlowObjectMatcher<TestValue> slowObjectMatcher = SlowObjectMatcher.create();
        final boolean matches = slowObjectMatcher.matches(test, pattern);

        assertThat(matches, Matchers.is(false));
    }

    @Test
    public void shouldNotMatchObjectWhenClassIsDifferent() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className("TestValue")
                .fieldMatcher(Patterns.string("field1").equalTo("test1"))
                .fieldMatcher(Patterns.string("field2").notEqualTo("test3"))
                .build();

        final NotTestValue test = new NotTestValue("test1", "test2");

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.create();
        final boolean matches = slowObjectMatcher.matches(test, pattern);

        assertThat(matches, Matchers.is(false));
    }

    @Test
    public void shouldFilterMatching() {

        final ObjectPattern pattern = ObjectPattern.builder()
                .className("TestValue")
                .fieldMatcher(Patterns.string("field1").equalTo("test1"))
                .fieldMatcher(Patterns.string("field2").notEqualTo("failed"))
                .build();

        final List<Object> list = List.of(
                new TestValue("test1", "success"),
                new TestValue("test1", "failed"),
                new TestValue("test2", "failed"),
                new NotTestValue("test1", "success"),
                new TestValue("test1", "success")
        );

        final SlowObjectMatcher<Object> slowObjectMatcher = SlowObjectMatcher.create();

        final List<Object> results = list.stream().filter(item -> slowObjectMatcher.matches(item, pattern)).collect(Collectors.toList());

        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder(new TestValue("test1", "success"), new TestValue("test1", "success")));
    }
}