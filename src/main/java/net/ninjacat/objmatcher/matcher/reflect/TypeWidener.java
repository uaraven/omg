package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public final class TypeWidener {
    private static final Set<Class> INT_CLASSES = HashSet.of(
            Integer.class,
            Long.class,
            Short.class,
            Byte.class,
            Character.class
    );
    private static final Set<Class> FLOAT_CLASSES = HashSet.of(
            Float.class,
            Double.class
    );


    /**
     * Widens type to one supported by matcher.
     * <p>
     * int, short, byte, char -> long
     * <p>
     * float -> double
     * <p>
     * All other types remain unchanged.
     *
     * @param aClass Type to widen
     * @return Widened type
     */
    public static Class widen(final Class<?> aClass) {
        if (INT_CLASSES.contains(aClass)) {
            return Long.class;
        } else if (FLOAT_CLASSES.contains(aClass)) {
            return Double.class;
        } else {
            return aClass;
        }
    }


}
