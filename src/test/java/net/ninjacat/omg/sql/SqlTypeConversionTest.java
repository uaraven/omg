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
}