package net.ninjacat.objmatcher.matcher.patterns;

import lombok.Value;

import java.util.List;

@Value
public class ObjectPattern {
    String className;
    List<FieldPattern<?>> fieldPatterns;

    public static ObjectPatternBuilder builder() {
        return new ObjectPatternBuilder();
    }
}
