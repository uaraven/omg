package net.ninjacat.objmatcher.matcher.reflect;

import net.ninjacat.objmatcher.matcher.errors.MatchingException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjectMetadata {
    private final Map<String, PropertyMetadata> fieldTypes = new HashMap<>();

    public ObjectMetadata(final Class objectClass) {
        fieldTypes.putAll(
                Arrays.stream(objectClass.getMethods())
                        .filter(this::isGetter)
                        .collect(Collectors.toMap(
                                method -> nameToFieldName(method.getName()),
                                PropertyMetadata::fromMethod
                        )));
    }

    public PropertyMetadata getFieldData(String fieldName) {
        if (fieldTypes.containsKey(fieldName)) {
            return fieldTypes.get(fieldName);
        } else {
            throw new MatchingException(String.format("Cannot get type of field '%s", fieldName));
        }
    }

    private String nameToFieldName(String name) {
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
                && (method.getName().startsWith("get") || method.getName().startsWith("is"));
    }

}
