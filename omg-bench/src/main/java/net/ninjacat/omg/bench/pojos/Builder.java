package net.ninjacat.omg.bench.pojos;


import io.vavr.collection.List;
import io.vavr.collection.Set;

import java.util.Random;

public class Builder {

    private static final Set<String> FIRST_NAMES = List.of("Alex",
            "Angus", "Eric", "Aiden", "Abdul", "Freddy", "Charles", "Marco", "Ciaran", "Oscar", "Zoya",
            "Hanna", "Kara", "Laila", "Caroline").toSet();
    private static final Set<String> LAST_NAMES = List.of("Turner", "Byrd", "Knight", "Patel", "Bolton", "Guerrero").toSet();
    private static final Set<String> JOB_NAMES = List.of("Seaman", "Carpenter", "Accountant", "Pilot",
            "Armoured Assault Vehicle Crew Member").toSet();

    public static final Random PRNG = new Random();

    public Entity generateEntity() {
        return Immutable
    }
}
