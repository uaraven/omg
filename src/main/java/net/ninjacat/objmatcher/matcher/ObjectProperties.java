package net.ninjacat.objmatcher.matcher;

import net.ninjacat.objmatcher.matcher.reflect.Property;

import java.util.List;

public interface ObjectProperties {
    Property getProperty(String fieldName);

    List<Property> getProperties();
}
