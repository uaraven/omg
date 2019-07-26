package net.ninjacat.objmatcher.matcher;

import net.ninjacat.objmatcher.matcher.reflect.Property;

import java.util.List;
import java.util.Optional;

public interface ObjectProperties {
    Optional<Property> getProperty(String fieldName);

    List<Property> getProperties();
}
