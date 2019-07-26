package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegerEqTest {

    @Test
    public void shouldMatch() {
        final IntegerEq byteField = new IntegerEq( 42L);

        assertThat(byteField.matches(42L), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final IntegerEq byteField = new IntegerEq( 41L);

        assertThat(byteField.matches(42L), is(false));
    }

}