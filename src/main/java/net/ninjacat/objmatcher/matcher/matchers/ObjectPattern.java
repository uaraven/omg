package net.ninjacat.objmatcher.matcher.matchers;

import lombok.Value;
import net.jcip.annotations.Immutable;

/**
 * Pattern to compare object to
 */
@Value
@Immutable
public class ObjectPattern {
    String className;
    Matcher<?> matcher;

    public static ObjectPatternBuilder forClassName(final String className) throws ClassNotFoundException {
        final Class<?> cls = Class.forName(className);
        return new ObjectPatternBuilder(cls);
    }

    public static ObjectPatternBuilder forClass(final Class cls) {
        return new ObjectPatternBuilder(cls);
    }
}
