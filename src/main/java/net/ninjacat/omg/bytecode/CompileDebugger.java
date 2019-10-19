/*
 * omg: CompileDebugger.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
