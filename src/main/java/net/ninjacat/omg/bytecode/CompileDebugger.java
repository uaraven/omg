package net.ninjacat.omg.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class CompileDebugger {

    private CompileDebugger() {
    }

    public static void dumpClass(final String fileName, final byte[] bytes) throws IOException {
        Files.write(Paths.get(fileName), bytes);
    }

    public static void verifyClass(final byte[] bytecode) {
        final StringWriter sw = new StringWriter();
        CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(sw));
        final String verificationLog = sw.toString();
        if (!verificationLog.isEmpty()) {
            System.err.println(verificationLog);
        }
    }
}
