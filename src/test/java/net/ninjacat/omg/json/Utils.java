package net.ninjacat.omg.json;

import java.io.*;
import java.util.stream.Collectors;

final class Utils {

    private Utils() {
    }

    static String getJson(final String resourceName) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/" + resourceName)))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
