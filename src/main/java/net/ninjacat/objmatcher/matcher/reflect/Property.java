package net.ninjacat.objmatcher.matcher.reflect;

import lombok.Value;
import net.jcip.annotations.Immutable;

import java.lang.reflect.Method;

@Value
@Immutable
public class Property {
    final String propertyName;
    final Class type;
    final Method getterMethod;

    /**
     * Creates a new property from a given getter method.
     * <p>
     * Property name is derived from Javabeans property naming conventions. If provided getter does not follow
     * convention, then property will be named the same as the getter.
     * <p>
     * If method accepts parameters, {@link IllegalArgumentException} will be thrown. Indexed properties are not supported
     *
     * @param method Method
     * @return
     */
    static Property fromMethod(final Method method) {
        return new Property(method.getReturnType(), method);
    }
}
