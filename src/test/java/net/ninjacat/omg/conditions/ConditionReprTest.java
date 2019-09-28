package net.ninjacat.omg.conditions;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * These tests exist purely to satisfy coverage tool.
 * <p>
 * as @{link Condition#repr()} methods are subject to change at any time and should only be regarded as helpers there
 * is little reason to cover them with extensive unit tests.
 * <p>
 * Theses tests merely check that repr() methods do execute and return string that vaguely resembles the condition.
 */
@RunWith(Theories.class)
public class ConditionReprTest {

    @Test
    @Theory
    public void testEqRepr(final ComparableValue value) {
        final String repr = Conditions.matcher().property("prop").eq(value.getValue()).build().repr();
        assertPattern(repr, "'.*' = '.*'");
    }

    @Test
    @Theory
    public void testGtRepr(final ComparableValue value) {
        final String repr = Conditions.matcher().property("prop").gt(value.getValue()).build().repr();
        assertPattern(repr, "'.*' > '.*'");
    }

    @Test
    @Theory
    public void testLtRepr(final ComparableValue value) {
        final String repr = Conditions.matcher().property("prop").lt(value.getValue()).build().repr();
        assertPattern(repr, "'.*' < '.*'");
    }

    @Test
    @Theory
    public void testNeqRepr(final ComparableValue value) {
        final String repr = Conditions.matcher().property("prop").neq(value.getValue()).build().repr();
        assertPattern(repr, "'.*' != '.*'");
    }

    @Test
    public void testStringEqRepr() {
        final String repr = Conditions.matcher().property("prop").eq("text").build().repr();
        assertPattern(repr, "'.*' = '.*'");
    }

    @Test
    public void testStringNeqRepr() {
        final String repr = Conditions.matcher().property("prop").neq("text").build().repr();
        assertPattern(repr, "'.*' != '.*'");
    }

    @Test
    public void testStringRegexRepr() {
        final String repr = Conditions.matcher().property("prop").regex("text").build().repr();
        assertPattern(repr, "'.*' ~= '.*'");
    }

    @Test
    public void testStringInRepr() {
        final String repr = Conditions.matcher().property("prop")
                .in(io.vavr.collection.List.of("A", "B", "C").asJava())
                .build()
                .repr();
        assertPattern(repr, "'prop' in '\\[.*\\]'");
    }

    @Test
    public void testAndRepr() {
        final String repr = Conditions.matcher()
                .property("prop1").gt(1)
                .property("prop2").lt(100)
                .build().repr();
        assertPattern(repr, "AND \\{.*\\}");
    }

    @Test
    public void testOrRepr() {
        final String repr = Conditions.matcher()
                .or(o -> o
                        .property("prop1").gt(1)
                        .property("prop2").lt(100)
                ).build().repr();
        assertPattern(repr, "OR \\{.*\\}");
    }

    @Test
    public void testNotRepr() {
        final String repr = Conditions.matcher()
                .not(o -> o.property("prop1").gt(1)).build().toString();
        assertPattern(repr, "NOT\\s*'.*' > '.*'");
    }

    private static void assertPattern(final String repr, final String pattern) {
        assertThat(String.format("Repr [%s] doesn't match pattern [%s]", repr, pattern),
                Pattern.compile(pattern, Pattern.MULTILINE + Pattern.DOTALL).matcher(repr).find(), is(true));
    }

    @FunctionalInterface
    private interface ValueProvider {
        Object getValue();
    }

    private enum ComparableValue implements ValueProvider {
        INT(10),
        LONG(10L),
        SHORT((short) 10),
        BYTE((byte) 10),
        CHAR((char) 50),
        DOUBLE(10.0);

        private final Object value;

        ComparableValue(final Object value) {
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

}