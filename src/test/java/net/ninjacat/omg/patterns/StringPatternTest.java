package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import org.immutables.value.Value;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Theories.class)
public class StringPatternTest {

    @Theory
    @Test
    public void testSimplePattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str2").eq("test")
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("string", "test"),
                getStringTest("string", "something"));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(getStringTest("string", "test")));
    }

    @Theory
    @Test
    public void testOrPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .or(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("whoops", "test"),
                getStringTest("string", "something"),
                getStringTest("not", "matching"));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getStringTest("whoops", "test"), getStringTest("string", "something")));
    }

    @Theory
    @Test
    public void testAndPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .and(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("whoops", "test"),
                getStringTest("string", "something"),
                getStringTest("string", "test"),
                getStringTest("not", "matching"));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getStringTest("string", "test")));
    }

    @Theory
    @Test
    public void testRegexPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str1").regex("st.*[abc]final")
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("st12final", "test"),
                getStringTest("stringcfinal", "something"),
                getStringTest("stop it afinal", "test"),
                getStringTest("stringisinfinal", ""));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                getStringTest("stringcfinal", "something"),
                getStringTest("stop it afinal", "test")));
    }

    @Theory
    @Test
    public void testNotPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .not(n -> n.property("str1").regex("st.*[abc]final"))
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("st12final", "test"),
                getStringTest("stringcfinal", "something"),
                getStringTest("stop it afinal", "test"),
                getStringTest("stringisinfinal", ""));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                getStringTest("st12final", "test"),
                getStringTest("stringisinfinal", "")));
    }


    @Theory
    @Test
    public void testNullPattern(final CompilerSelectionStrategy strategy) {
        final Condition condition = Conditions.matcher()
                .property("str1").eq(null)
                .build();

        final Pattern<StringPattern> pattern = Patterns.compile(condition, PatternCompiler.forClass(StringPattern.class, strategy));

        final List<StringPattern> tests = List.of(getStringTest("st12final", "test"),
                getStringTest(null, "something"),
                getStringTest("stop it afinal", "test"));

        final java.util.List<StringPattern> result = tests.filter(pattern).asJava();

        assertThat(result, contains(getStringTest(null, "something")));
    }

    private static StringPattern getStringTest(final String s1, final String s2) {
        return ImmutableStringPattern.of(s1, s2);
    }


    @Value.Immutable
    @Value.Style(allParameters = true)
    public interface StringPattern {
        @Nullable
        String getStr1();

        String getStr2();
    }
}