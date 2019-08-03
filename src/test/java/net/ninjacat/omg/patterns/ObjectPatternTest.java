package net.ninjacat.omg.patterns;

import lombok.Value;
import net.jcip.annotations.Immutable;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class ObjectPatternTest {

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
        testComplexPattern(strategy);
    }

    private static void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").match(obj -> obj.property("aString").eq("found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    private static void testComplexPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("inner").match(obj ->
                        obj.or(orCond -> orCond
                                .property("aString").eq("found it")
                                .property("aString").regex(".*works.*"))
                )
                .property("name").neq("Waldo")
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, PatternCompiler.forClass(TestClass.class, strategy));

        final List<TestClass> testList =
                io.vavr.collection.List.of(
                        new TestClass(new InnerClass(1, "found it"), "Waldo"),
                        new TestClass(new InnerClass(1, "works!"), "Waldo"),
                        new TestClass(new InnerClass(1, "found it"), "Joseph"),
                        new TestClass(new InnerClass(1, "it works!"), "Yoda")
                ).asJava();

        final List<TestClass> matched = testList.stream().filter(pattern::matches).collect(Collectors.toList());

        assertThat(matched, hasItems(
                new TestClass(new InnerClass(1, "found it"), "Joseph"),
                new TestClass(new InnerClass(1, "it works!"), "Yoda")
        ));
    }

    @Value
    @Immutable
    public static class InnerClass {
        int anInt;
        String aString;
    }

    @Value
    public static class TestClass {
        private InnerClass inner;
        String name;
    }
}
