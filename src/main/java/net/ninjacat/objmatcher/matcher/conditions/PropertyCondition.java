package net.ninjacat.objmatcher.matcher.conditions;

public interface PropertyCondition<T> extends Condition {
    String getField();

    T getValue();
}
