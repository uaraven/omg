package net.ninjacat.objmatcher.matcher.matchers;

import io.vavr.collection.List;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LogicalAndMatcherTest {

    @Test
    public void shouldMatchOneOfValues() {
        final TypedMatcher<Long> matcher = new LogicalAndMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerGt(5)
                ).asJava()
        );

        assertThat(matcher.matches(10L), is(true));
    }

    @Test
    public void shouldMatchNoneOfValues() {
        final TypedMatcher<Long> matcher = new LogicalAndMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerGt(5)
                ).asJava()
        );

        assertThat(matcher.matches(7L), is(false));
    }
}