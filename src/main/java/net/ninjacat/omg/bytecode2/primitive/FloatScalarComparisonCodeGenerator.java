/*
 * omg: IntCodeGenerator.java
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

package net.ninjacat.omg.bytecode2.primitive;

import io.vavr.API;
import net.ninjacat.omg.bytecode2.TypedCodeGenerator;
import net.ninjacat.omg.bytecode2.generator.CodeGenerationContext;
import net.ninjacat.omg.bytecode2.generator.Codes;
import net.ninjacat.omg.conditions.ConditionMethod;
import net.ninjacat.omg.conditions.PropertyCondition;
import net.ninjacat.omg.errors.CompilerException;
import org.objectweb.asm.MethodVisitor;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static org.objectweb.asm.Opcodes.*;

public class FloatScalarComparisonCodeGenerator<T> implements TypedCodeGenerator<T, Float, Float> {

    private final CodeGenerationContext context;

    FloatScalarComparisonCodeGenerator(final CodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void getMatchingConstant(final PropertyCondition<Float> condition, final MethodVisitor method) {
        Codes.pushFloat(method, condition.getValue());
    }

    @Override
    public void compare(final PropertyCondition<Float> condition, final MethodVisitor method) {
        final int opcode = API.Match(condition.getMethod()).of(
                Case($(ConditionMethod.EQ), eq -> IFEQ),
                Case($(ConditionMethod.NEQ), eq -> IFNE),
                Case($(ConditionMethod.GT), eq -> IFLT),
                Case($(ConditionMethod.LT), eq -> IFGT),
                Case($(), () -> {
                            throw new CompilerException("Unsupported Condition for float type: %s", condition);
                        }
                ));
        method.visitInsn(FCMPL);
        Codes.compareWithOpcode(method, opcode);
    }
}
