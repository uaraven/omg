package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringEqTest {
    @Test
    public void shouldMatch() {
        final StringEq field = new StringEq("matches");

        assertThat(field.matches("matches"), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final StringEq field = new StringEq("matches");

        assertThat(field.matches("not matches"), is(false));
    }

}