package net.ninjacat.omg.bench.pojos;


import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public final class Builder {

    private static final Set<String> FIRST_NAMES = Sets.newHashSet("Alex",
            "Angus", "Eric", "Aiden", "Abdul", "Freddy", "Charles", "Marco", "Ciaran", "Oscar", "Zoya",
            "Hanna", "Kara", "Laila", "Caroline");
    private static final Set<String> LAST_NAMES = Sets.newHashSet("Turner", "Byrd", "Knight", "Patel", "Bolton", "Guerrero");
    private static final Set<String> JOB_NAMES = Sets.newHashSet("Seaman", "Carpenter", "Accountant", "Pilot",
            "Armoured Assault Vehicle Crew Member", "Armoured Assault Vehicle Commander");

    private static final Iterator<String> firstNames = Iterators.cycle(FIRST_NAMES);
    private static final Iterator<String> lastNames = Iterators.cycle(LAST_NAMES);
    private static final Iterator<String> jobs = Iterators.cycle(JOB_NAMES);
    private static final Iterator<Integer> experiences = Iterators.cycle(1, 2, 3, 4, 5, 6, 7, 8, 9);
    private static final Iterator<Integer> ages = Iterators.cycle(22, 21, 43, 30, 60, 55, 32, 27, 29);
    private static final Iterator<Gender> genders = Iterators.cycle(EnumSet.allOf(Gender.class));

    private Builder() {
    }


    public static Entity sameEntity() {
        final Job job = ImmutableJob.builder().name("Accountant").experience(30).build();
        return ImmutableEntity.builder()
                .firstName("Oscar")
                .lastName("Knight")
                .age(33)
                .gender(Gender.UNDISCLOSED)
                .qualification(job)
                .build();
    }


    public static Entity generateEntity() {
        final Job job = ImmutableJob.builder().name(jobs.next()).experience(experiences.next()).build();
        return ImmutableEntity.builder()
                .firstName(firstNames.next())
                .lastName(lastNames.next())
                .age(ages.next())
                .gender(genders.next())
                .qualification(job)
                .build();
    }

    public static Stream<Entity> entities(final long limit) {
        return Stream.iterate(generateEntity(), e -> generateEntity()).limit(limit);
    }
}
