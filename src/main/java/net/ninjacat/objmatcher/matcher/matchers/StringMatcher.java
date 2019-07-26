package net.ninjacat.objmatcher.matcher.matchers;

public interface StringMatcher extends TypedMatcher<String> {

    @Override
    default Class getExpectedType() {
        return String.class;
    }
}
