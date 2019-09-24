package net.ninjacat.omg.omql;

import org.immutables.value.Value;

import java.util.Collection;

@Value.Immutable
public interface QueryContext {
    TypeValidator validator();

    Collection<Class<?>> allowedSources();
}
