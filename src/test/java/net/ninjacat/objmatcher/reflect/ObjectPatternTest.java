package net.ninjacat.objmatcher.reflect;

import lombok.Value;
import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.conditions.Condition;
import net.ninjacat.objmatcher.conditions.Conditions;
import net.ninjacat.objmatcher.patterns.Pattern;
import net.ninjacat.objmatcher.patterns.Patterns;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ObjectPatternTest {

    @Test
    public void testSimpleMatching() {
        final Condition condition = Conditions.start()
                .property("inner").match(obj -> obj.property("aString").eq("found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

        final TestClass testObj = new TestClass(new InnerClass(1, "found it"), "Waldo");

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    @Test
    public void testLogicalMatching() {
        final Condition condition = Conditions.start()
                .property("inner").match(obj ->
                        obj.or(orCond -> orCond
                                .property("aString").eq("found it")
                                .property("aString").regex(".*works.*"))
                )
                .property("name").neq("Waldo")
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(TestClass.class));

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
    private static class InnerClass {
        int anInt;
        String aString;
    }

    @Value
    private static class TestClass {
        private InnerClass inner;
        String name;
    }
}
