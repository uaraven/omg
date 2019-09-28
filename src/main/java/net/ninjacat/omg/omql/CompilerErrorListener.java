package net.ninjacat.omg.omql;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilerErrorListener extends BaseErrorListener {
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
        syntaxErrors.add(ImmutableSyntaxError.builder().offendingSymbol(offendingSymbol).line(line).position(charPositionInLine).message(msg).build());
    }

    boolean hasErrors() {
        return !syntaxErrors.isEmpty();
    }

    @Override
    public String toString() {
        return syntaxErrors.stream().map(SyntaxError::toString).collect(Collectors.joining("\n"));
    }
}
