package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.collection.Map;
import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.matcher.ObjectProperties;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Extracts and contains information on properties in the class.
 * <p>
 * Only public getters are considered. Getters are expected to follow standard Javabeans convention, i.e.
 * method name is prefixed with "get" or "is".
 * <p>
 * Indexed properties are not supported.
 */
@Immutable
public class DefaultObjectProperties implements ObjectProperties {
    private final Map<String, Property> fieldTypes;

    public DefaultObjectProperties(final Class objectClass) {
        fieldTypes = io.vavr.collection.HashMap.ofAll(Arrays.stream(objectClass.getMethods())
                .filter(DefaultObjectProperties::isGetter)
                .map(Property::fromMethod)
                .collect(Collectors.toMap(
                        Property::getPropertyName,
                        Function.identity()
                )));
    }

    @Override
    public Optional<Property> getProperty(final String fieldName) {
        return Optional.ofNullable(fieldTypes
                .get(fieldName)
                .getOrNull());
    }

    @Override
    public List<Property> getProperties() {
        return fieldTypes.values().asJava();
    }

    private static boolean isGetter(final Method method) {
        return !method.getReturnType().equals(Void.class)
                && !method.getReturnType().equals(Class.class)
                && method.getParameterCount() == 0
                && (method.getName().startsWith("get") || method.getName().startsWith("is"));
    }

}
