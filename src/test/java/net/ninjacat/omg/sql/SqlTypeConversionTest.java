package net.ninjacat.omg.sql;

import net.ninjacat.omg.errors.SqlParsingException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SqlTypeConversionTest {

    @Test
    public void shouldExtractStringFromDoubleQuotes() {
        final String value = SqlTypeConversion.extractString("\"value\"");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromSingleQuotes() {
        final String value = SqlTypeConversion.extractString("'value'");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromDoubleQuotesChecked() {
        final String value = SqlTypeConversion.extractStringChecked("\"value\"");

        assertThat(value, is("value"));
    }

    @Test
    public void shouldExtractStringFromSingleQuotesChecked() {
        final String value = SqlTypeConversion.extractStringChecked("'value'");

        assertThat(value, is("value"));
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailToExtractStringWithNoQuotes() {
        SqlTypeConversion.extractStringChecked("value");
    }

    @Test(expected = SqlParsingException.class)
    public void shouldFailToExtractStringWithUnmatchedQuotes() {
        SqlTypeConversion.extractStringChecked("\"value'");
    }

    @Test
    public void shouldConvertStringToInteger() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(Integer.class, "42");
        assertThat(result, is(42));
    }

    @Test
    public void shouldConvertStringToInt() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(int.class, "42");
        assertThat(result, is(42));
    }

    @Test
    public void shouldConvertStringToLong() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(long.class, "42");
        assertThat(result, is(42L));
    }


    @Test
    public void shouldConvertStringToShort() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(short.class, "42");
        assertThat(result, is((short) 42));
    }

    @Test
    public void shouldConvertStringToDouble() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(Double.class, "42.42");
        assertThat(result, is(42.42));
    }

    @Test
    public void shouldConvertStringToPrimitiveDouble() {
        final Object result = SqlTypeConversion.toJavaTypeStrict(double.class, "42");
        assertThat(result, is(42.0));
    }
}