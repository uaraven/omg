package net.ninjacat.omg.bytecode;

public class CompiledClassLoader extends ClassLoader {

    public Class<?> defineClass(final String name, final byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }
}
