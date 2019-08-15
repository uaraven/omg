package net.ninjacat.omg.bench.pojos;

import org.immutables.value.Value;

@Value.Immutable
public interface Job {
    String name();
    int experience();
    double rating();
}
