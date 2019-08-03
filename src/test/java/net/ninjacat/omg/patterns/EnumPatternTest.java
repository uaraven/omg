package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class EnumPatternTest {


    @Test
    public void testReflection() {
        testAll(CompilerSelectionStrategy.SAFE);
    }

    @Test
    public void testCompiled() {
        testAll(CompilerSelectionStrategy.FAST);
    }

    private static void testAll(final CompilerSelectionStrategy strategy) {
        testSimplePattern(strategy);
    }

    private static void testSimplePattern(final CompilerSelectionStrategy strategy) {
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