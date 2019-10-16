package net.ninjacat.omg.omql;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.errors.OmqlSecurityException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains list of classes that are registered to be used in {@code FROM} clause of query.
 * <p>
 * Registered classes can be used by their short name.
 */
class RegisteredQuerySources {
    private final Map<String, Class<?>> sources;

    RegisteredQuerySources(final Collection<Class<?>> classes, final boolean registerDependencies) {
        this.sources = (registerDependencies
                ? buildDependencies(classes)
                : noDependencies(classes)).collect(Collectors.toConcurrentMap(Tuple2::_1, Tuple2::_2));

    }

    private Stream<Tuple2<String, Class<?>>> noDependencies(Collection<Class<?>> classes) {
        return classes.stream()
                .distinct()
                .flatMap(RegisteredQuerySources::namedPair);
    }

    /**
     * Retrieves an instance of a class used in FROM clause.
     * <p>
     * This will first look up class in the list of registered classes, if neither short nor full name of any of
     * registered classes matches source in FROM clause of the query then attempt to load that class by name will be made.
     *
     * @param source Source name used in FROM clause of the query
     * @return Class which can be used to type-check query conditions
     * @throws OmqlParsingException if Class specified in FROM cannot be found
     */
    Class<?> getSource(final String source) {
        final Try<Class<?>> firstTry = Try.of(() -> Optional.of(sources.get(source)).get());
        return firstTry
                .orElse(Try.of(() -> tryCreate(source)))
                .getOrElseThrow(ex -> rethrow(ex, source));
    }

    private Class<?> tryCreate(final String className) throws ClassNotFoundException {
        final Class<?> cls = Class.forName(className);
        if (isAllowedClass(cls)) {
            return cls;
        } else {
            throw new OmqlSecurityException("Class '%' is not allowed", cls);
        }
    }

    private OmqlSecurityException rethrow(Throwable ex, final String source) {
        return ex instanceof OmqlSecurityException
                ? (OmqlSecurityException) ex
                : new OmqlSecurityException("Class '%s' is not registered for matching", source);
    }

    private Stream<Tuple2<String, Class<?>>> buildDependencies(final Collection<Class<?>> classes) {
        return classes.stream()
                .flatMap(this::getClassDependencies)
                .distinct()
                .flatMap(RegisteredQuerySources::namedPair);
    }

    private static Stream<Tuple2<String, Class<?>>> namedPair(final Class<?> cls) {
        return Stream.of(
                new Tuple2<>(cls.getSimpleName(), cls),
                new Tuple2<>(cls.getName(), cls)
        );
    }

    private Stream<Class<?>> getClassDependencies(final Class<?> cls) {
        return Stream.concat(
                Stream.of(cls),
                getDependentClasses(cls)
        );
    }

    private Stream<Class<?>> getDependentClasses(final Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .map(Method::getReturnType)
                .filter(this::isSupportedClass)
                .map(c -> (Class<?>) c);
    }

    private boolean isAllowedClass(final Class<?> cls) {
        return cls.isPrimitive() || cls.getName().startsWith("java.");
    }

    private boolean isSupportedClass(final Class<?> cls) {
        return !cls.isPrimitive() && !cls.getName().startsWith("java.");
    }
}
