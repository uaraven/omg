package net.ninjacat.objmatcher.matcher.reflect;

import lombok.Value;
import net.ninjacat.objmatcher.matcher.ObjectMetadata;
import org.junit.Test;

public class ObjectMetadataTest {
    @Test
    public void shouldFindAllGetters() {
        final ObjectMetadata objectMetadata = new DefaultObjectMetadata(TestObject.class);
        objectMetadata.
    }

    @Value
    private static class TestObject {
        String strField;
        int intField;
        boolean enabled;

        int getIndexed(int index) {
            return 0;
        }
    }
}