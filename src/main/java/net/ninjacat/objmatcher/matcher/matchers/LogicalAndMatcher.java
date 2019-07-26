package net.ninjacat.objmatcher.matcher.matchers;

import net.ninjacat.objmatcher.utils.Memoize;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LogicalAndMatcher<T> extends LogicalMatcher<T> {
    private final Supplier<String> repr = Memoize.that(this::generateToString);

    private String generateToString() {
        return "AND [\n" + getChildMatchers()
                .stream()
                .map(matcher -> "  " + matcher.toString()).collect(Collectors.joining("\n")) +
                "\n]";
    }

    protected LogicalAndMatcher(final List<? extends Matcher<T>> matchers) {
        super(matchers);
    }

    @Override
    public boolean matches(final T checkedValue) {
        return getChildMatchers().stream().allMatch(matcher -> matcher.matches(checkedValue));
    }

    @Override
    public String toString() {
        return repr.get();
    }
}
