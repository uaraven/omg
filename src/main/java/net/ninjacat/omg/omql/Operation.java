/*
 * omg: Operation.java
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

package net.ninjacat.omg.omql;

import io.vavr.control.Option;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Operation {
    EQ("=", new EqProducer()),
    NEQ1("!=", Constants.NEQ_PRODUCER),
    NEQ2("<>", Constants.NEQ_PRODUCER),
    GT(">", new GtProducer()),
    LT("<", new LtProducer()),
    GTE(">=", new GteProducer()),
    LTE("<=", new LteProducer()),
    REGEX("~=", new RegexProducer()),
    IN("in", new InProducer()),
    MATCH("match", new MatchProducer());

    private final String opCode;
    private final OmqlConditionProducer producer;

    private static final Map<String, Operation> opCodeMap =
            Arrays.stream(values()).collect(Collectors.toMap(
                    Operation::getOpCode,
                    Function.identity()
            ));

    Operation(final String opCode, final OmqlConditionProducer producer) {
        this.opCode = opCode;
        this.producer = producer;
    }

    public String getOpCode() {
        return opCode;
    }

    public OmqlConditionProducer getProducer() {
        return producer;
    }

    public static Option<Operation> byOpCode(final String opCode) {
        return Option.of(opCodeMap.get(opCode));
    }

    private static class Constants {
        private static final NeqProducer NEQ_PRODUCER = new NeqProducer();
    }
}
