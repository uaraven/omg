package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringNotEqTest {
    @Test
    public void shouldMatch() {
        final StringNotEq field = new StringNotEq("field", "matches");

        assertThat(field.matches("not matches"), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final StringNotEq field = new StringNotEq("field", "matches");

        assertThat(field.matches("matches"), is(false));
    }

}