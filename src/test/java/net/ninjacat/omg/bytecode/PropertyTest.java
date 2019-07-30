package net.ninjacat.omg.bytecode;

import lombok.Value;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyTest {

    @Test
    public void name() {
    }

    @Value
    private static class TestObject {
        int field;
    }

    private <T extends TestObject> int testMethod(T instance) {
        return instance.getField();
    }
}