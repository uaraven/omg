package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.collection.Map;
import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.matcher.ObjectMetadata;
import net.ninjacat.objmatcher.matcher.errors.MatcherException;

import java.lang.reflect.Method;
import java.util.Arrays;
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
public class DefaultObjectMetadata implements ObjectMetadata {
    private final Map<String, Property> fieldTypes;

    public DefaultObjectMetadata(final Class objectClass) {
        fieldTypes = io.vavr.collection.HashMap.ofAll(Arrays.stream(objectClass.getMethods())
                        .filter(this::isGetter)
                        .collect(Collectors.toMap(
                                method -> nameToFieldName(method.getName()),
                                Property::fromMethod
                        )));
    }

    @Override
    public Property getProperty(final String fieldName) {
        return fieldTypes
                .get(fieldName)
                .getOrElseThrow(() -> new MatcherException("Cannot get type of field '%s", fieldName));
    }

    private String nameToFieldName(final String name) {
        final String fieldNamePascalCase;
        if (name.startsWith("get")) {
            fieldNamePascalCase = name.substring(3);
        } else if (name.startsWith("is")) {
            fieldNamePascalCase = name.substring(2);
        } else {
            fieldNamePascalCase = name;
        }
        return fieldNamePascalCase.substring(0,1).toLowerCase() + fieldNamePascalCase.substring(1);
    }

    private boolean isGetter(final Method method) {
        return !method.getReturnType().equals(Void.class)
                && method.getParameterCount() == 0
                && (method.getName().startsWith("get") || method.getName().startsWith("is"));
    }

}
