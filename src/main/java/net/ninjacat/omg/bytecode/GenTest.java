package net.ninjacat.omg.bytecode;

/**
 * This class is used to inspect generated bytecode
 * TODO: Delete
 */
public class GenTest {

    public GenTest() {

    }

    public boolean compare() {

        Object b = new Integer(1);
        int c = (Integer)b;
        return false;
    }

    private boolean longCmp(long l1, long l2) {
        return l1 == l2;
    }
}
