package net.ninjacat.omg.bytecode;

import net.jcip.annotations.Immutable;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import org.immutables.value.Value;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ObjectPatternTest {

    @Test
    public void testSimpleMatching() {
        final Condition condition = Conditions.matcher()
                .property("inner").match(obj -> obj.property("aString").eq("found it"))
                .build();

        final Pattern<TestClass> pattern = Patterns.compile(condition, AsmPatternCompiler.forClass(TestClass.class));

        final TestClass testObj = ImmutableTestClass.builder().inner(ImmutableInnerClass.builder().anInt(1).aString("found it").build())
                .name("Waldo").build();

        final boolean match = pattern.matches(testObj);

        assertThat(match, is(true));
    }

    @Value.Immutable
    @Immutable
    public interface InnerClass {
        int getAnInt();

        String getAString();
    }

    @Value.Immutable
    public interface TestClass {
        InnerClass getInner();

        String getName();
    }
}
