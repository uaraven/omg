package net.ninjacat.omg.patterns;

public final class AlwaysMatchingPattern<T> implements Pattern<T> {

    static final AlwaysMatchingPattern INSTANCE = new AlwaysMatchingPattern();

    private AlwaysMatchingPattern() {
    }

    @Override
    public boolean matches(final T instance) {
        return true;
    }

    @Override
    public String toString() {
        return "true";
    }
}
