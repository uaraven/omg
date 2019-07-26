package net.ninjacat.objmatcher.matcher.matchers;

@FunctionalInterface
public interface IntMatcher extends TypedMatcher<Long> {

    @Override
    default Class getExpectedType() {
        return Long.class;
    }
}
