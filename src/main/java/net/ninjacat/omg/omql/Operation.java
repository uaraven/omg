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
