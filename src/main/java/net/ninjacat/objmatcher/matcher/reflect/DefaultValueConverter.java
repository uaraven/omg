package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.control.Try;
import lombok.Value;
import net.ninjacat.objmatcher.matcher.MatchingException;
import net.ninjacat.objmatcher.matcher.TypeConverter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

public class DefaultValueConverter implements TypeConverter.ValueConverter {
    private static final MethodHandle BOX_INT = converterHandle("toLong", int.class, Long.class);
    private static final MethodHandle BOX_DOUBLE = converterHandle("toDouble", double.class, Double.class);

    private static final Map<ConversionKey, MethodHandle> CONVERTERS = Map.of(
            ConversionKey.of(int.class, Long.class), BOX_INT,
            ConversionKey.of(short.class, Long.class), BOX_INT,
            ConversionKey.of(byte.class, Long.class), BOX_INT,
            ConversionKey.of(char.class, Long.class), BOX_INT,
            ConversionKey.of(Integer.class, Long.class), BOX_INT,
            ConversionKey.of(Short.class, Long.class), BOX_INT,
            ConversionKey.of(Byte.class, Long.class), BOX_INT,
            ConversionKey.of(Character.class, Long.class), BOX_INT,
            ConversionKey.of(Float.class, Double.class), BOX_DOUBLE
    );
    private final Object value;

    public DefaultValueConverter(final Object value) {
        this.value = value;
    }

    @Override
    public Object to(final Class targetClass) {
        final ConversionKey key = ConversionKey.of(value.getClass(), targetClass);
        if (CONVERTERS.containsKey(key)) {
            return Try.of(() -> CONVERTERS.get(key).invoke(value))
                    .getOrElseThrow((ex) -> new MatchingException(String.format(
                            "Failed to convert value '%s' of type '%s' to '%s'", value, value.getClass().getSimpleName(), targetClass), ex));
        }
        throw new MatchingException(String.format(
                "Cannot find conversion for value '%s' of type '%s' to '%s'", value, value.getClass().getSimpleName(), targetClass));
    }

    private static MethodHandle converterHandle(final String name, final Class source, final Class target) {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final MethodType type = MethodType.methodType(target, source);
        try {
            return lookup.findStatic(DefaultValueConverter.class, name, type);
        } catch (final Exception e) {
            throw new IllegalStateException(String.format("Cannot initialize type mapper from '%s' to '%s'",
                    source, target), e);
        }
    }

    private static Long toLong(final int intValue) {
        return (long) intValue;
    }

    private static Double toDouble(final double doubleValue) {
        return doubleValue;
    }

    @Value
    private static class ConversionKey {
        Class source;
        Class target;

        static ConversionKey of(final Class source, final Class target) {
            return new ConversionKey(source, target);
        }
    }
}
