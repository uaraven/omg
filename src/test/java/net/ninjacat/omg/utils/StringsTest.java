package net.ninjacat.omg.utils;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringsTest {
    @Test
    public void shouldIndentString() {
        final String indented = Strings.indent("abc", 3);

        assertThat(indented, is("   abc"));
    }
}