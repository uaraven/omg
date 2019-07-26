package net.ninjacat.objmatcher.matcher.patterns;

import io.vavr.collection.List;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LogicalAndMatcherTest {

    @Test
    public void shouldMatchOneOfValues() {
        final Matcher<Long> matcher = new LogicalAndMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerGt(5)
                ).asJava()
        );

        assertThat(matcher.matches(10L), is(true));
    }

    @Test
    public void shouldMatchNoneOfValues() {
        final Matcher<Long> matcher = new LogicalAndMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerGt(5)
                ).asJava()
        );

        assertThat(matcher.matches(7L), is(false));
    }
}