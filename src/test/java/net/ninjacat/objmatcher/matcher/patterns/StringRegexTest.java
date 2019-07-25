package net.ninjacat.objmatcher.matcher.patterns;

import net.ninjacat.objmatcher.matcher.SlowObjectMatcher;
import net.ninjacat.objmatcher.matcher.TestValue;
import org.junit.Test;

import java.util.function.Predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringRegexTest {

    @Test
    public void shouldMatchRegex() {
        final ObjectPattern pattern = ObjectPattern.builder()
                .className("TestValue")
                .fieldMatcher(Patterns.string("field1").matches("Hello.*!"))
                .build();

        final TestValue matching = new TestValue("Hello, world!", "something something");
        final TestValue nonMatching = new TestValue("Hello, world", "something something");

        final Predicate<TestValue> matcher = SlowObjectMatcher.forPattern(pattern);

        final boolean m1 = matcher.test(matching);
        final boolean m2 = matcher.test(nonMatching);

        assertThat(m1, is(true));
        assertThat(m2, is(false));
    }
}