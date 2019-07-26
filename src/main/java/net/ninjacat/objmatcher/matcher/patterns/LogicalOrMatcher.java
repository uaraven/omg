package net.ninjacat.objmatcher.matcher.patterns;

import net.ninjacat.objmatcher.utils.Memoize;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LogicalOrMatcher<T> extends LogicalMatcher<T> {
    private final Supplier<String> repr = Memoize.that(this::generateToString);

    private String generateToString() {
        return "OR [\n" + getChildMatchers()
                .stream()
                .map(matcher -> "  " + matcher.toString()).collect(Collectors.joining("\n")) +
                "\n]";
    }

    protected LogicalOrMatcher(final List<? extends Matcher<T>> matchers) {
        super(matchers);
    }

    @Override
    public boolean matches(final T checkedValue) {
        return getChildMatchers().stream().anyMatch(matcher -> matcher.matches(checkedValue));
    }

    @Override
    public String toString() {
        return repr.get();
    }
}
