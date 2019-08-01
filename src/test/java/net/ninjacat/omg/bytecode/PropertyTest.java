package net.ninjacat.omg.bytecode;

import lombok.Data;
import lombok.Value;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyTest {

    @Test
    public void name() {
    }

    public static class TestObject {
        int field;

        public TestObject(final int field) {
            this.field = field;
        }

        public int getField() {
            return field;
        }
    }

    public static class TestObject2 extends TestObject {
        public TestObject2(int field) {
            super(field);
        }
    }

    public <T extends TestObject2> Integer testMethod(T instance) {
        return instance.getField();
    }
}