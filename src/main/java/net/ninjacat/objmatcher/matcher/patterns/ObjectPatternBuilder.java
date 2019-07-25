package net.ninjacat.objmatcher.matcher.patterns;

import java.util.ArrayList;
import java.util.List;

public class ObjectPatternBuilder {
    private String className;
    private final List<FieldPattern<?>> fieldPatterns = new ArrayList<>();

    public ObjectPatternBuilder className(final String className) {
        this.className = className;
        return this;
    }

    public ObjectPatternBuilder fieldMatcher(final FieldPattern matcher) {
        fieldPatterns.add(matcher);
        return this;
    }

    public ObjectPattern build() {
        return new ObjectPattern(className, List.copyOf(fieldPatterns));
    }
}
