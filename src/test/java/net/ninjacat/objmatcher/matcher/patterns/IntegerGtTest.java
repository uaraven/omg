package net.ninjacat.objmatcher.matcher.patterns;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegerGtTest {

    @Test
    public void shouldMatch() {
        final IntegerGt byteField = new IntegerGt( 42L);

        assertThat(byteField.matches(43L), is(true));
    }

    @Test
    public void shouldNotMatch() {
        final IntegerGt byteField = new IntegerGt( 42L);

        assertThat(byteField.matches(42L), is(false));
    }

}