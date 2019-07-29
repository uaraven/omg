package net.ninjacat.objmatcher.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Memoize<T> implements Supplier<T> {
    private static final Object key = new Object();

    private final Supplier<T> supplier;
    private final Map<Object, T> holder = new ConcurrentHashMap<>();

    public static <T> Memoize<T> that(final Supplier<T> supplier) {
        return new Memoize<>(supplier);
    }

    private Memoize(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return holder.computeIfAbsent(key, (k) -> supplier.get());
    }
}
