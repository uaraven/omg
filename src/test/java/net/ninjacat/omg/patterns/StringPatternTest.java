package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StringPatternTest {

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
        testOrPattern(strategy);
        testAndPattern(strategy);
        testRegexPattern(strategy);
        testNullPattern(strategy);
    }

    private static void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str2").eq("test")
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringTest.class, strategy));

        final List<StringTest> tests = List.of(new StringTest("string", "test"),
                new StringTest("string", "something"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new StringTest("string", "test")));
    }

    private static void testOrPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringTest.class, strategy));

        final List<StringTest> tests = List.of(new StringTest("whoops", "test"),
                new StringTest("string", "something"),
                new StringTest("not", "matching"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new StringTest("whoops", "test"), new StringTest("string", "something")));
    }

    private static void testAndPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringTest.class, strategy));

        final List<StringTest> tests = List.of(new StringTest("whoops", "test"),
                new StringTest("string", "something"),
                new StringTest("string", "test"),
                new StringTest("not", "matching"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new StringTest("string", "test")));
    }

    private static void testRegexPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str1").regex("st.*[abc]final")
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringTest.class, strategy));

        final List<StringTest> tests = List.of(new StringTest("st12final", "test"),
                new StringTest("stringcfinal", "something"),
                new StringTest("stop it afinal", "test"),
                new StringTest("stringisinfinal", ""));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                new StringTest("stringcfinal", "something"),
                new StringTest("stop it afinal", "test")));
    }

    private static void testNullPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str1").eq(null)
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringTest.class, strategy));

        final List<StringTest> tests = List.of(new StringTest("st12final", "test"),
                new StringTest(null, "something"),
                new StringTest("stop it afinal", "test"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new StringTest(null, "something")));
    }


    @Value
    public static class StringTest {
        private String str1;
        private String str2;
    }
}