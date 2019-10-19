/*
 * omg: TypeConversionException.java
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

package net.ninjacat.omg.errors;

public class TypeConversionException extends OmgException {

    public <T> TypeConversionException(final Throwable cause, final T value, final Class targetType) {
        super(cause, "Cannot convert '%s %s' to '%s'", value.getClass().getName(), value, targetType.getName());
    }

    public <T> TypeConversionException(final Class valueType, final T value, final Class targetType) {
        super("Cannot convert '%s %s' to '%s'", valueType.getName(), value, targetType.getName());
    }

}
