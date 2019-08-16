package net.ninjacat.omg.bench;

import net.ninjacat.omg.CompilerSelectionStrategy;
import net.ninjacat.omg.PatternCompiler;
import net.ninjacat.omg.bench.pojos.Builder;
import net.ninjacat.omg.bench.pojos.Entity;
import net.ninjacat.omg.conditions.Condition;
import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.patterns.Pattern;
import net.ninjacat.omg.patterns.Patterns;
import org.openjdk.jmh.annotations.*;

public class Runner {

    private static Pattern<Entity> generatePattern(final CompilerSelectionStrategy strategy) {
        final Condition cond = Conditions.matcher().or(o -> o
                .property("firstName").eq("Freddy")
                .property("firstName").eq("Caroline")
                .property("firstName").eq("Oscar")
                .property("firstName").eq("Zoya"))
                .property("qualification").match(
                        Conditions.matcher().or(o1 -> o1.
                                property("name").regex("Armoured Assault Vehicle.*")
                        ).build()
                )
                .build();
        return Patterns.compile(cond, PatternCompiler.forClass(Entity.class, strategy));
    }

    @State(Scope.Thread)
    public static class CState {
        Pattern<Entity> reflectPattern;
        Pattern<Entity> bytecodePattern;

        @Setup(Level.Trial)
        public void init() {
            reflectPattern = generatePattern(CompilerSelectionStrategy.SAFE);
            bytecodePattern = generatePattern(CompilerSelectionStrategy.FAST);
        }
    }

    @Benchmark
    public boolean filterReflectStable(final CState state) {
        return state.reflectPattern.matches(Builder.sameEntity());
    }

    @Benchmark

    public boolean filterBytecodeStable(final CState state) {
        return state.bytecodePattern.matches(Builder.sameEntity())  ;
    }


    @Benchmark
    public boolean filterReflectChanging(final CState state) {
        return state.reflectPattern.matches(Builder.generateEntity());
    }

    @Benchmark

    public boolean filterBytecodeChanging(final CState state) {
        return state.bytecodePattern.matches(Builder.generateEntity());
    }
}
