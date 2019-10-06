package net.ninjacat.omg.reflect;

import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.CompilingStrategy;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.PatternCompiler;
import net.ninjacat.omg.patterns.Patterns;
import org.hamcrest.Matchers;
import org.immutables.value.Value;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectCompilerTest {

    @Test
    public void shouldCompileBooleanProperty() {
        final Condition condition = Conditions.matcher()
                .property("boolField").eq(true)
                .build();

        final Pattern<BoolPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(BoolPattern.class, CompilingStrategy.SAFE));

        assertThat(pattern, Matchers.instanceOf(BooleanEqPattern.class));
    }


    @Value.Immutable
    public interface BoolPattern {
        @Value.Parameter
        boolean getBoolField();
    }
}
