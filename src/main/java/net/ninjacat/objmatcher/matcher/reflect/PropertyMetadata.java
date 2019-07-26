package net.ninjacat.objmatcher.matcher.reflect;

import lombok.Value;

import java.lang.reflect.Method;

@Value
public class PropertyMetadata {
    final Class type;
    final Method getterMethod;

    static PropertyMetadata fromMethod(final Method method) {
        return new PropertyMetadata(method.getReturnType(), method);
    }
}
