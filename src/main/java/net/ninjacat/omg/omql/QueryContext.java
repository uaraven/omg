package net.ninjacat.omg.omql;

import org.immutables.value.Value;

@Value.Immutable
public interface QueryContext {
    TypeValidator validator();

    RegisteredQuerySources registeredSources();
}
