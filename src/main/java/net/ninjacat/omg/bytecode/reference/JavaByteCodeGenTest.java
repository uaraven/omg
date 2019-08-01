package net.ninjacat.omg.bytecode.reference;

import java.util.regex.Pattern;

public class JavaByteCodeGenTest {

    public void test() {
        Pattern p = Pattern.compile(".*");
        boolean b = p.matcher("test").matches();
    }
}
