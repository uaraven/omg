package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegerLtTest {

    @Test
    public void shouldMatch() {
        final IntegerLt byteField = new IntegerLt("byteField", 42L);

        assertThat(byteField.matches(41L), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final IntegerLt byteField = new IntegerLt("byteField", 42L);

        assertThat(byteField.matches(42L), is(false));
    }

}