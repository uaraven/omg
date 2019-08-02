package net.ninjacat.omg.patterns;

import io.vavr.collection.List;
import lombok.Value;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import net.ninjacat.omg.reflect.ReflectPatternCompiler;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StringPatternTest {

    @Test
    public void testSimplePattern() {
        final Condition condition = Conditions.matcher()
                .property("str2").eq("test")
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(StringTest.class));

        final List<StringTest> tests = List.of(new StringTest("string", "test"),
                new StringTest("string", "something"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(new StringTest("string", "test")));
    }

    @Test
    public void testOrPattern() {
        final Condition condition = Conditions.matcher()
                .or(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(StringTest.class));

        final List<StringTest> tests = List.of(new StringTest("whoops", "test"),
                new StringTest("string", "something"),
                new StringTest("not", "matching"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new StringTest("whoops", "test"), new StringTest("string", "something")));
    }

    @Test
    public void testAndPattern() {
        final Condition condition = Conditions.matcher()
                .and(orCond -> orCond
                        .property("str1").eq("string")
                        .property("str2").eq("test"))
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(StringTest.class));

        final List<StringTest> tests = List.of(new StringTest("whoops", "test"),
                new StringTest("string", "something"),
                new StringTest("string", "test"),
                new StringTest("not", "matching"));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(new StringTest("string", "test")));
    }

    @Test
    public void testRegexPattern() {
        final Condition condition = Conditions.matcher()
                .property("str1").regex("st.*[abc]final")
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(StringTest.class));

        final List<StringTest> tests = List.of(new StringTest("st12final", "test"),
                new StringTest("stringcfinal", "something"),
                new StringTest("stop it afinal", "test"),
                new StringTest("stringisinfinal", ""));

        final java.util.List<StringTest> result = tests.filter(pattern).asJava();

        assertThat(result, contains(
                new StringTest("stringcfinal", "something"),
                new StringTest("stop it afinal", "test")));
    }

    @Test
    public void testNullPattern() {
        final Condition condition = Conditions.matcher()
                .property("str1").eq(null)
                .build();

        final Pattern<StringTest> pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(StringTest.class));

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