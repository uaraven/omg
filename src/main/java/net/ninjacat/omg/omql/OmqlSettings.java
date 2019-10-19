/*
 * omg: OmqlSettings.java
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
