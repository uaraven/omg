package net.ninjacat.omg.omql;

import java.util.EnumSet;

/**
 * Security settings for OMQL compiler
 */
public enum OmqlSettings {
    /**
     * If included into option set will allow matching on registered classes and all properties' types.
     * Otherwise matching is allowed only for classes explicitly registered when creating {@link QueryCompiler}.
     */
    REGISTER_PROPERTY_TYPES;

    /**
     * Minimal security. Allows automatic whitelisting of properties' types.
     *
     * @return Set of settings for minimal security
     */
    public static EnumSet<OmqlSettings> relaxed() {
        return EnumSet.of(REGISTER_PROPERTY_TYPES);
    }

    /**
     * Strict security. Restricts matching only to explicitly registered types.
     *
     * @return Set of settings for maximal supported security
     */
    public static EnumSet<OmqlSettings> strict() {
        return EnumSet.noneOf(OmqlSettings.class);
    }

    /**
     * Creates a set of settings from provided values
     *
     * @param first First security setting
     * @param rest  Optional rest of settings
     * @return Set of settings.
     */
    public static EnumSet<OmqlSettings> of(final OmqlSettings first, final OmqlSettings... rest) {
        return EnumSet.of(first, rest);
    }
}
