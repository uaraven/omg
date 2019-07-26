package net.ninjacat.objmatcher.matcher.reflect;

import lombok.Value;
import net.ninjacat.objmatcher.matcher.ObjectProperties;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;

public class ObjectMetadataTest {
    @Test
    public void shouldFindAllGetters() {
        final ObjectProperties objectMetadata = new DefaultObjectMetadata(TestObject.class);
        final List<Property> properties = objectMetadata.getProperties();

        final List<String> propNames = properties.stream().map(Property::getPropertyName).collect(Collectors.toList());

        assertThat(propNames, Matchers.containsInAnyOrder("strField", "intField", "enabled"));

    }

    @Value
    private static class TestObject {
        String strField;
        int intField;
        boolean enabled;

        int getIndexed(final int index) {
            return 0;
        }
    }
}