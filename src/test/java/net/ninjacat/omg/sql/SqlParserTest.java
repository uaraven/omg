package net.ninjacat.omg.sql;

import org.junit.Test;

public class SqlParserTest {
    @Test
    public void name() {
        new SqlParser("select name, age from data where age > 25");
    }
}