package net.ninjacat.omg.bytecode;


public class IntPropertyRetrievalCompilerTest {

    @org.junit.Test
    public void name() throws Exception {
        final Property<Test> prop = Property.fromPropertyName("field", Test.class);

        final byte[] compiled = new IntPropertyRetrievalCompiler().compile(prop);

        final Class<?> aClass = new ClassLoader(getClass().getClassLoader()) {
            public Class<?> loadGenerated() throws ClassNotFoundException {
                return super.defineClass("net.ninjacat.omg.bytecode.IntPropRetriever", compiled, 0, compiled.length);
            }
        }.loadGenerated();
    }

}