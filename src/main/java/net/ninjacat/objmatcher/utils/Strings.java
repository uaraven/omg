package net.ninjacat.objmatcher.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Strings {
    private Strings() {
    }

    public static String indent(final String input, final int indent) {
        final String[] lines = input.split("\n");
        return Arrays.stream(lines).map((st) -> {
            if (indent == 0) return input;
            else return String.format("%" + indent + "s", "") + input;
        }).collect(Collectors.joining("\n"));
    }
}
