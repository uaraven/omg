package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringRegexTest {

    @Test
    public void shouldMatch() {
        final StringRegex field = new StringRegex("Hello.*!");

        assertThat(field.matches("Hello, world!"), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final StringRegex field = new StringRegex("Hello.*!");

        assertThat(field.matches("Hello, world"), is(false));
    }
}