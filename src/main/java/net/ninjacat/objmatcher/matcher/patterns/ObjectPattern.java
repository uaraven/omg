package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;
import net.jcip.annotations.Immutable;

import java.util.List;

/**
 * Pattern to compare object to
 */
@Value
@Immutable
public class ObjectPattern {
    String className;
    List<FieldPattern<?>> fieldPatterns;

    public static ObjectPatternBuilder builder() {
        return new ObjectPatternBuilder();
    }
}
