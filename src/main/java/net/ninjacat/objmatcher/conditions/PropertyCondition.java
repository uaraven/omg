package net.ninjacat.objmatcher.conditions;

public interface PropertyCondition<T> extends Condition {
    String getProperty();

    T getValue();
}
