package net.ninjacat.omg.patterns;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Theories.class)
public class AccessorsTest {

    @Theory
    public void shouldMatchOnMethodCall(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("otherValue").eq(42)
                .build();

        final Pattern<TestMethod> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestMethod.class, strategy));

        final boolean matches = pattern.matches(new TestMethod());

        assertThat(matches, is(true));
    }

    @Theory
    public void shouldMatchOnGetter(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("value").eq(42)
                .build();

        final Pattern<TestGetter> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestGetter.class, strategy));

        final boolean matches = pattern.matches(new TestGetter());

        assertThat(matches, is(true));
    }

    @Theory
    public void shouldMatchOnInterfaceGetter(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("value").eq(42)
                .build();

        final Pattern<TestInterface> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestInterface.class, strategy));

        final boolean matches = pattern.matches(new TestIntf());

        assertThat(matches, is(true));
    }


    public static class TestGetter {
        public int getValue() {
            return 42;
        }
    }

    public static class TestMethod {
        public int otherValue() {
            return 42;
        }
    }


    @FunctionalInterface
    public interface TestInterface {
        int getValue();
    }

    public static class TestIntf implements TestInterface {
        @Override
        public int getValue() {
            return 42;
        }
    }
}
