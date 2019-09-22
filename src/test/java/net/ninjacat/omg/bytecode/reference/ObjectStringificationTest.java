package net.ninjacat.omg.bytecode.reference;

import net.ninjacat.omg.bytecode.AsmPatternCompiler;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.patterns.PropertyPattern;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectStringificationTest {

    @Test
    public void shouldMatchSimpleRegex() {
        final PropertyCondition<String> condition = createPropertyCondition(ConditionMethod.REGEX, ".*2019-09-01");

        final PropertyPattern<ObjectStringificationTest.StringifiedTest> pattern = AsmPatternCompiler.forClass(StringifiedTest.class).build(condition);

        assertThat(pattern.matches(new StringifiedTest(LocalDate.of(2019, 9, 1).atStartOfDay())), is(true));
        assertThat(pattern.matches(new StringifiedTest(LocalDate.of(2019, 9, 13).atStartOfDay())), is(false));
    }


    private static PropertyCondition<String> createPropertyCondition(final ConditionMethod method, final String value) {
        return new PropertyCondition<String>() {

            @Override
            public String repr(final int level) {
                return "";
            }

            @Override
            public ConditionMethod getMethod() {
                return method;
            }

            @Override
            public String getProperty() {
                return "value";
            }

            @Override
            public String getValue() {
                return value;
            }
        };
    }

    public static class Value {
        private final LocalDateTime dt;

        public Value(final LocalDateTime dt) {
            this.dt = dt;
        }

        @Override
        public String toString() {
            return "Date value:" + dt.format(DateTimeFormatter.ISO_DATE);
        }
    }

    public static class StringifiedTest {
        private final Value value;

        public StringifiedTest(final LocalDateTime dt) {
            this.value = new Value(dt);
        }

        public Value getValue() {
            return value;
        }
    }

}
