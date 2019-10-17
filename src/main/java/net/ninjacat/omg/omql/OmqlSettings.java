package net.ninjacat.omg.omql;

/**
 * Security settings for OMQL compiler
 */
public final class OmqlSettings {

    private final boolean registerPropertyTypes;

    private OmqlSettings(final OmqlSettings.Builder builder) {
        this.registerPropertyTypes = builder.isRegisterPropertyClasses();
    }

    /**
     * Creates a new builder to configure OMQL settings
     *
     * @return new instance of {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Preconfigured settings for easy usage.
     * This setting enables automatic registration of property types.
     *
     * @return Settings configured for ease of use
     */
    public static OmqlSettings easy() {
        return OmqlSettings.builder().registerPropertyClasses(true).build();
    }

    /**
     * Preconfigured settings for maximum security
     * This setting disables automatic registration of property types.
     *
     * @return Settings configured for maximum security
     */
    public static OmqlSettings strict() {
        return OmqlSettings.builder().registerPropertyClasses(false).build();
    }

    /**
     * Returns {@code true} if automatic registration of property types is enabled
     *
     * @return {@code true} if automatic registration of property types is enabled
     */
    public boolean isRegisterPropertyTypes() {
        return registerPropertyTypes;
    }

    /**
     * Fluid-style builder for OmqlSettings
     */
    public static final class Builder {
        private boolean registerPropertyClasses;

        public Builder() {
            this.registerPropertyClasses = false;
        }

        public Builder(final OmqlSettings settings) {
            this.registerPropertyClasses = settings.isRegisterPropertyTypes();
        }

        boolean isRegisterPropertyClasses() {
            return registerPropertyClasses;
        }

        /**
         * Enables or disables automatic registration of property types
         *
         * @param registerPropertyClasses flag to either enable or disable automatic property type registration
         * @return this builder
         */
        public Builder registerPropertyClasses(final boolean registerPropertyClasses) {
            this.registerPropertyClasses = registerPropertyClasses;
            return this;
        }

        /**
         * Creates OmqlSettings instance
         *
         * @return OmqlSettings instance set up according to this builder
         */
        public OmqlSettings build() {
            return new OmqlSettings(this);
        }
    }
}
