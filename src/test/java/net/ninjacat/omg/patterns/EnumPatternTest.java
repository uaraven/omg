package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(Theories.class)
public class EnumPatternTest {

    @Test
    @Theory
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("e1").eq(Enum1.VALUE1)
                .build();

        final Pattern<EnumTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(EnumTest.class, strategy));

        final List<EnumTest> tests = List.of(
                new EnumTest(Enum1.VALUE1),
                new EnumTest(Enum1.VALUE2));

        final java.util.List<EnumTest> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new EnumTest(Enum1.VALUE1)));
    }

    public enum Enum1 {
        VALUE1,
        VALUE2
    }

    @Value
    public static class EnumTest {
        private Enum1 e1;
    }
}