package net.ninjacat.objmatcher.matcher.matchers;


import lombok.Value;
import net.ninjacat.objmatcher.matcher.ObjectProperties;
import net.ninjacat.objmatcher.matcher.errors.MatcherException;
import net.ninjacat.objmatcher.matcher.reflect.DefaultObjectProperties;
import net.ninjacat.objmatcher.matcher.reflect.Property;
import net.ninjacat.objmatcher.matcher.reflect.PropertyMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectPatternBuilder {
    private final String className;
    private ObjectProperties properties;
    private List<? extends Matcher> matchers = new ArrayList<>();

    ObjectPatternBuilder(Class cls) {
        this.properties = new DefaultObjectProperties(cls);
        this.className = cls.getName();
    }

    public IntegerPatternBuilder integer(String fieldName) {
        final Property property = properties.getProperty(fieldName)
                .orElseThrow(() -> new MatcherException("Cannot find field '%s' in class '%s", fieldName, className));
        return new IntegerPatternBuilder(fieldName);
    }


    public ObjectPattern build() {
        return new ObjectPattern(className, Collections.unmodifiableList(matchers));
    }

    @Value
    public static class IntegerPatternBuilder {
        String fieldName;

        public PropertyMatcher<Long> equalTo(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerEq(value));
        }

        public PropertyMatcher<Long> notEqualTo(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerNotEq(value));
        }

        public PropertyMatcher<Long> lessThan(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerLt(value));
        }

        public PropertyMatcher<Long> greaterThan(final long value) {
            return new PropertyMatcher<>(fieldName, new IntegerGt(value));
        }
    }

}
