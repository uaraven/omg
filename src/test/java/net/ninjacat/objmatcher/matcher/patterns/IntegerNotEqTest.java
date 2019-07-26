package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegerNotEqTest {

    @Test
    public void shouldMatch() {
        final IntegerNotEq byteField = new IntegerNotEq(42L);

        assertThat(byteField.matches(41L), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final IntegerNotEq byteField = new IntegerNotEq(42L);

        assertThat(byteField.matches(42L), is(false));
    }

}