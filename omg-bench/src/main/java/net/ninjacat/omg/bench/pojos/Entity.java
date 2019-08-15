package net.ninjacat.omg.bench.pojos;

import org.immutables.value.Value;

@Value.Immutable
public interface Entity {
    String firstName();
    String lastName();
    int age();
    Gender gender();
    Job qualification();
}
