package net.ninjacat.omg.omql;

import net.ninjacat.omg.errors.OmqlParsingException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.stream.Collectors;

final class AntlrTools {

    private AntlrTools() {
    }

    static void assertError(final List<ParseTree> items) {
        final List<String> errorList = items.stream()
                .filter(it -> it instanceof ErrorNode)
                .map(ParseTree::getText)
                .collect(Collectors.toList());
        if (!errorList.isEmpty()) {
            throw new OmqlParsingException("Failed to parse: \n%s", String.join("", errorList));
        }
    }
}
