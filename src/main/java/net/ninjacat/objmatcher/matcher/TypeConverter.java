package net.ninjacat.objmatcher.matcher;

public interface TypeConverter {
    ValueConverter convert(Object value);

    interface ValueConverter {
        Object to(Class targetClass);
    }
}
