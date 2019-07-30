package net.ninjacat.omg.conditions;

public interface PropertyCondition<T> extends Condition {
    String getProperty();

    T getValue();
}
