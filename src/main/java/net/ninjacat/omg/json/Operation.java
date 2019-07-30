package net.ninjacat.omg.json;

import io.vavr.control.Option;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Operation {
    EQ("eq", new EqProducer()),
    NEQ("neq", new NeqProducer()),
    GT("gt", new GtProducer()),
    LT("lt", new LtProducer()),
    REGEX("regex", new RegexProducer()),
    MATCH("match", new MatchProducer());

    private final String opCode;
    private final PropertyConditionProducer producer;

    private static final Map<String, Operation> opCodeMap =
            Arrays.stream(values()).collect(Collectors.toMap(
                    kv -> kv.opCode,
                    Function.identity()
            ));

    Operation(final String opCode, final PropertyConditionProducer producer) {
        this.opCode = opCode;
        this.producer = producer;
    }

    public String getOpCode() {
        return opCode;
    }

    public PropertyConditionProducer getProducer() {
        return producer;
    }

    public static Option<Operation> byOpCode(final String opCode) {
        return Option.of(opCodeMap.get(opCode));
    }
}
