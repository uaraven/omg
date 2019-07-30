package net.ninjacat.omg.reflect;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EnumPatternTest {

    @Test
    public void testSimplePattern() {
        final Condition condition = Conditions.start()
                .property("e1").eq("VALUE1")
                .build();

        final Pattern<EnumTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(EnumTest.class));

        final List<EnumTest> tests = List.of(new EnumTest(Enum1.VALUE1),
                new EnumTest(Enum1.VALUE2));

        final java.util.List<EnumTest> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new EnumTest(Enum1.VALUE1)));
    }

    private enum Enum1 {
        VALUE1,
        VALUE2
    }

    @Value
    private static class EnumTest {
        private Enum1 e1;
    }
}