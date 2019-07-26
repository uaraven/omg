package net.ninjacat.objmatcher.matcher.patterns;

import io.vavr.collection.List;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LogicalOrMatcherTest {

    @Test
    public void shouldMatchOneOfValues() {
        final Matcher<Long> matcher = new LogicalOrMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerEq(20),
                        new IntegerEq(30)
                ).asJava()
        );

        assertThat(matcher.matches(20L), is(true));
    }

    @Test
    public void shouldMatchNoneOfValues() {
        final Matcher<Long> matcher = new LogicalOrMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerEq(20),
                        new IntegerEq(30)
                ).asJava()
        );

        assertThat(matcher.matches(25L), is(false));
    }
}