/*
 * omg: RegisteredQuerySources.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.omql;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.errors.OmqlSecurityException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Contains list of classes that are registered to be used in {@code FROM} clause of query.
 * <p>
 * Registered classes can be used by their short name.
 */
class RegisteredQuerySources {
    private final Map<String, Class<?>> sources;

    RegisteredQuerySources(final Collection<Class<?>> classes, final boolean registerDependencies) {
        final Map<String, Class<?>> collection = new ConcurrentHashMap<>();
        (registerDependencies
                ? buildDependencies(classes)
                : noDependencies(classes)).forEachOrdered(tpl -> collection.putIfAbsent(tpl._1, tpl._2));
        this.sources = Collections.unmodifiableMap(collection);
    }

    private static Stream<Tuple2<String, Class<?>>> noDependencies(final Collection<Class<?>> classes) {
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

    private static Class<?> tryCreate(final String className) throws ClassNotFoundException {
        final Class<?> cls = Class.forName(className);
        if (isAllowedClass(cls)) {
            return cls;
        } else {
            throw new OmqlSecurityException("Class '%' is not allowed", cls);
        }
    }

    private static OmqlSecurityException rethrow(final Throwable ex, final String source) {
        return ex instanceof OmqlSecurityException
                ? (OmqlSecurityException) ex
                : new OmqlSecurityException("Class '%s' is not registered for matching", source);
    }

    private static Stream<Tuple2<String, Class<?>>> buildDependencies(final Collection<Class<?>> classes) {
        return classes.stream()
                .flatMap(RegisteredQuerySources::getClassDependencies)
                .distinct()
                .flatMap(RegisteredQuerySources::namedPair);
    }

    private static Stream<Tuple2<String, Class<?>>> namedPair(final Class<?> cls) {
        return Stream.of(
                new Tuple2<>(cls.getSimpleName(), cls),
                new Tuple2<>(cls.getName(), cls)
        );
    }

    private static Stream<Class<?>> getClassDependencies(final Class<?> cls) {
        return Stream.concat(
                Stream.of(cls),
                getDependentClasses(cls)
        );
    }

    private static Stream<Class<?>> getDependentClasses(final Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .map(Method::getReturnType)
                .filter(RegisteredQuerySources::isSupportedClass)
                .map(c -> (Class<?>) c);
    }

    private static boolean isAllowedClass(final Class<?> cls) {
        return cls.isPrimitive() || cls.getName().startsWith("java.");
    }

    private static boolean isSupportedClass(final Class<?> cls) {
        return !cls.isPrimitive() && !cls.getName().startsWith("java.");
    }
}
