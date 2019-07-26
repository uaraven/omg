package net.ninjacat.objmatcher.matcher.matchers;

import io.vavr.collection.List;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LogicalOrMatcherTest {

    @Test
    public void shouldMatchOneOfValues() {
        final TypedMatcher<Long> matcher = new LogicalOrMatcher<>(
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
        final TypedMatcher<Long> matcher = new LogicalOrMatcher<>(
                List.of(
                        new IntegerEq(10),
                        new IntegerEq(20),
                        new IntegerEq(30)
                ).asJava()
        );

        assertThat(matcher.matches(25L), is(false));
    }
}