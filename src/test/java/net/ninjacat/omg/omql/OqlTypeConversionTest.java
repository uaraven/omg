package net.ninjacat.omg.omql;

import net.ninjacat.omg.errors.OmqlParsingException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OqlTypeConversionTest {

    @Test
    public void shouldExtractStringFromDoubleQuotes() {
        final String value = OmqlTypeConversion.extractString("\"value\"");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromSingleQuotes() {
        final String value = OmqlTypeConversion.extractString("'value'");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromDoubleQuotesChecked() {
        final String value = OmqlTypeConversion.extractStringChecked("\"value\"");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromSingleQuotesChecked() {
        final String value = OmqlTypeConversion.extractStringChecked("'value'");

        assertThat(value, is("value"));
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailToExtractStringWithNoQuotes() {
        OmqlTypeConversion.extractStringChecked("value");
    }

    @Test(expected = OmqlParsingException.class)
    public void shouldFailToExtractStringWithUnmatchedQuotes() {
        OmqlTypeConversion.extractStringChecked("\"value'");
    }

    @Test
    public void shouldConvertStringToInteger() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(Integer.class, "42");
        assertThat(result, is(42));
    }

    @Test
    public void shouldConvertStringToInt() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(int.class, "42");
        assertThat(result, is(42));
    }

    @Test
    public void shouldConvertStringToLong() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(long.class, "42");
        assertThat(result, is(42L));
    }


    @Test
    public void shouldConvertStringToShort() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(short.class, "42");
        assertThat(result, is((short) 42));
    }

    @Test
    public void shouldConvertStringToDouble() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(Double.class, "42.42");
        assertThat(result, is(42.42));
    }

    @Test
    public void shouldConvertStringToPrimitiveDouble() {
        final Object result = OmqlTypeConversion.toJavaTypeStrict(double.class, "42");
        assertThat(result, is(42.0));
    }
}