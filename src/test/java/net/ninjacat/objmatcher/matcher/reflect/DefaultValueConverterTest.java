package net.ninjacat.objmatcher.matcher.reflect;

import net.ninjacat.objmatcher.matcher.TypeConverter;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DefaultValueConverterTest {

    private TypeConverter.ValueConverter converter(final Object value) {
        return new DefaultValueConverter(value);
    }

    @Test
    public void convertShortToInt() {
        final Object result = converter((short) 42).to(Long.class);
        assertThat(result, instanceOf(Long.class));
        assertThat(result, is(42L));
    }

    @Test
    public void convertByteToInt() {
        final Object result = converter((byte) 42).to(Long.class);
        assertThat(result, instanceOf(Long.class));
        assertThat(result, is(42L));
    }

    @Test
    public void convertCharToInt() {
        final Object result = converter((char) 42).to(Long.class);
        assertThat(result, instanceOf(Long.class));
        assertThat(result, is(42L));
    }

    @Test
    public void convertFloatToDouble() {
        final Object result = converter(42.5f).to(Double.class);
        assertThat(result, instanceOf(Double.class));
        assertThat(result, is(42.5));
    }
}